package com.example.eletriccarapp.ui

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.eletriccarapp.R
import java.net.HttpURLConnection
import java.net.URL

class CalcularAutonomiaActivity : AppCompatActivity() {
    lateinit var preco: EditText
    lateinit var kmPercorrido: EditText
    lateinit var btnCalcular: Button
    lateinit var resultado: TextView
    lateinit var btnclose: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calcular_autonomia)
        setupView()
        setupListener()
        setupCacheResult()
    }

    private fun setupCacheResult() {
        val valorCalculado = getSharedPref()
        resultado.text = valorCalculado.toString()
    }

    //Resgatar os valores nos campos
    fun setupView() {
        preco = findViewById(R.id.et_preco_kwh)
        kmPercorrido = findViewById(R.id.et_km_percorrido)
        btnCalcular = findViewById(R.id.btn_calcular)
        resultado = findViewById(R.id.tv_resultado)
        btnclose = findViewById(R.id.iv_close)
    }


    fun setupListener() {
        btnCalcular.setOnClickListener {
            calcular()
        }
        btnclose.setOnClickListener() {
            finish()
        }
    }

    fun calcular() {
        val preco = preco.text.toString().toFloat()
        val km = kmPercorrido.text.toString().toFloat()
        val result = preco / km
        resultado.text = result.toString()
        saveSharedPref(result)
    }

    fun saveSharedPref(resultado: Float) {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putFloat(getString(R.string.saved_calc), resultado)
            apply()
        }
    }

    fun getSharedPref(): Float {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getFloat(getString(R.string.saved_calc), 0.0f)

    }
}
