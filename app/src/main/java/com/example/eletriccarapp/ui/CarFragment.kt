package com.example.eletriccarapp.ui

import android.content.ContentValues
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.contentValuesOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.eletriccarapp.R
import com.example.eletriccarapp.data.CarFactory
import com.example.eletriccarapp.data.CarsApi
import com.example.eletriccarapp.data.local.CarRepository
import com.example.eletriccarapp.data.local.CarrosContract
import com.example.eletriccarapp.data.local.CarrosContract.CarEntry.COLLUMN_NAME_BATERIA
import com.example.eletriccarapp.data.local.CarrosContract.CarEntry.COLLUMN_NAME_POTENCIA
import com.example.eletriccarapp.data.local.CarrosContract.CarEntry.COLLUMN_NAME_PRECO
import com.example.eletriccarapp.data.local.CarrosContract.CarEntry.COLLUMN_NAME_RECARGA
import com.example.eletriccarapp.data.local.CarrosContract.CarEntry.COLLUMN_NAME_URL_PHOTO
import com.example.eletriccarapp.data.local.CarrosContract.CarEntry.TABLE_NAME
import com.example.eletriccarapp.data.local.CarsDbHelper
import com.example.eletriccarapp.domain.Carro
import com.example.eletriccarapp.ui.adapter.CarAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class CarFragment : Fragment() {
    lateinit var listaCarros: RecyclerView
    lateinit var progress: ProgressBar
    lateinit var noInternetImage: ImageView
    lateinit var noInternetText: TextView
    lateinit var carsApi: CarsApi

    var carrosArray: ArrayList<Carro> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.car_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRetrofit()
        setupView(view)
    }

    override fun onResume() {
        super.onResume()
        if (checkForInternet(context)) {
            gettAllCars()
            //CallService() -> outra forma de chamar serviços
        } else {
            emptyState()
        }
    }

    fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://igorbag.github.io/cars-api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        carsApi = retrofit.create(CarsApi::class.java)
    }

    fun gettAllCars() {
        carsApi.getAllCars().enqueue(object : Callback<List<Carro>> {
            override fun onResponse(call: Call<List<Carro>>, response: Response<List<Carro>>) {
                if (response.isSuccessful) {
                    progress.isVisible = false
                    noInternetImage.isVisible = false
                    noInternetText.isVisible = false
                    response.body()?.let {
                        setupList(it)
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Algo deu errado, por favor tente novamente mais tarde",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Carro>>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Algo deu errado, por favor tente novamente mais tarde",
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }

    fun emptyState() {
        progress.isVisible = false
        listaCarros.isVisible = false
        noInternetImage.isVisible = true
        noInternetText.isVisible = true
    }

    fun setupView(view: View) {
        listaCarros = view.findViewById(R.id.rv_lista_carros)
        progress = view.findViewById(R.id.pb_loader)
        noInternetImage = view.findViewById(R.id.iv_empty_state)
        noInternetText = view.findViewById(R.id.tv_no_wifi)

    }

    fun setupList(lista: List<Carro>) {
        val carroAdapter = CarAdapter(lista)
        listaCarros.apply {
            isVisible = true
            adapter = carroAdapter
        }
        carroAdapter.carItemLister = { carro ->
            val isSaved = CarRepository(requireContext()).saveIfNotExist(carro)
        }

    }

    fun CallService() {
        progress.isVisible = true
        MyTask().execute("https://igorbag.github.io/cars-api/cars.json")
    }

    fun checkForInternet(context: Context?): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }

    }

    // Utilizado o retrofit como abstração do AsyncTask
    inner class MyTask : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun doInBackground(vararg url: String?): String {
            var urlConnection: HttpURLConnection? = null
            try {
                val urlBase = URL(url[0])

                urlConnection = urlBase.openConnection() as HttpURLConnection
                urlConnection.connectTimeout = 60000
                urlConnection.readTimeout = 60000
                urlConnection.setRequestProperty(
                    "Accept", "application/json"
                )

                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    var response = urlConnection.inputStream.bufferedReader().use { it.readText() }
                    publishProgress(response)
                } else {
                    Log.e("Erro", "Serviço indisponivel no momento...")
                }

            } catch (ex: Exception) {
                Log.e("Erro", "Erro ao realizar processamento...")
            } finally {
                urlConnection?.disconnect()
            }
            return " "
        }

        override fun onProgressUpdate(vararg values: String?) {
            try {
                val jsonArray = JSONTokener(values[0]).nextValue() as JSONArray
                for (i in 0 until jsonArray.length()) {
                    val id = jsonArray.getJSONObject(i).getString("id")
                    val preco = jsonArray.getJSONObject(i).getString("preco")
                    val bateria = jsonArray.getJSONObject(i).getString("bateria")
                    val potencia = jsonArray.getJSONObject(i).getString("potencia")
                    val recarga = jsonArray.getJSONObject(i).getString("recarga")
                    val urlPhoto = jsonArray.getJSONObject(i).getString("urlPhoto")

                    val model = Carro(
                        id = id.toInt(),
                        preco = preco,
                        bateria = bateria,
                        potencia = potencia,
                        recarga = recarga,
                        urlPhoto = urlPhoto,
                        isFavorite = false
                    )
                    carrosArray.add(model)
                }
                progress.isVisible = false
                noInternetImage.isVisible = false
                noInternetText.isVisible = false
                //  setupList()
            } catch (ex: Exception) {
                Log.e("Erro", ex.message.toString())
            }
        }

    }

}
