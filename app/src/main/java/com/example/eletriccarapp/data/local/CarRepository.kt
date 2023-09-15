package com.example.eletriccarapp.data.local

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.eletriccarapp.data.local.CarrosContract.CarEntry.COLLUMN_NAME_BATERIA
import com.example.eletriccarapp.data.local.CarrosContract.CarEntry.COLLUMN_NAME_CAR_ID
import com.example.eletriccarapp.data.local.CarrosContract.CarEntry.COLLUMN_NAME_POTENCIA
import com.example.eletriccarapp.data.local.CarrosContract.CarEntry.COLLUMN_NAME_PRECO
import com.example.eletriccarapp.data.local.CarrosContract.CarEntry.COLLUMN_NAME_RECARGA
import com.example.eletriccarapp.data.local.CarrosContract.CarEntry.COLLUMN_NAME_URL_PHOTO
import com.example.eletriccarapp.domain.Carro

class CarRepository(private val context: Context) {

    fun save(carro: Carro): Boolean {
        var isSaved = false
        try {
            val dbHelper = CarsDbHelper(context)
            val db = dbHelper.writableDatabase

            val values = ContentValues().apply {
                put(COLLUMN_NAME_CAR_ID, carro.id)
                put(COLLUMN_NAME_PRECO, carro.preco)
                put(COLLUMN_NAME_BATERIA, carro.bateria)
                put(COLLUMN_NAME_POTENCIA, carro.potencia)
                put(COLLUMN_NAME_RECARGA, carro.recarga)
                put(COLLUMN_NAME_URL_PHOTO, carro.urlPhoto)
            }
            val inserted = db?.insert(CarrosContract.CarEntry.TABLE_NAME, null, values)

            if (inserted != null) {
                isSaved = true
            }
        } catch (ex: Exception) {
            Log.e("Error", "Erro ao inserir os dados")
        }
        return isSaved
    }

    fun findCarById(id: Int): Carro {
        val dbHelper = CarsDbHelper(context)
        val db = dbHelper.readableDatabase

        //Listagem das colunas a serem exibidas no resultado da Query
        val columns = arrayOf(
            BaseColumns._ID,
            COLLUMN_NAME_CAR_ID,
            COLLUMN_NAME_PRECO,
            COLLUMN_NAME_BATERIA,
            COLLUMN_NAME_POTENCIA,
            COLLUMN_NAME_RECARGA,
            COLLUMN_NAME_URL_PHOTO
        )

        val filter = "${COLLUMN_NAME_CAR_ID} = ?"
        val filterValues = arrayOf(id.toString())

        val cursor = db.query(
            CarrosContract.CarEntry.TABLE_NAME, //nome da tabela
            columns, // as colunas a serem exibidas
            filter, // where(filtro)
            filterValues, // valor do where, substituindo o paramentro ?
            null,
            null,
            null
        )

        var itemId: Long = 0
        var preco = ""
        var bateria = ""
        var potencia = ""
        var recarga = ""
        var urlPhoto = ""

        with(cursor) {
            while (moveToNext()) {
                itemId = getLong(getColumnIndexOrThrow(COLLUMN_NAME_CAR_ID))
                preco = getString(getColumnIndexOrThrow(COLLUMN_NAME_PRECO))
                bateria = getString(getColumnIndexOrThrow(COLLUMN_NAME_BATERIA))
                potencia = getString(getColumnIndexOrThrow(COLLUMN_NAME_POTENCIA))
                recarga = getString(getColumnIndexOrThrow(COLLUMN_NAME_RECARGA))
                urlPhoto = getString(getColumnIndexOrThrow(COLLUMN_NAME_URL_PHOTO))
            }
        }
        cursor.close()
        return Carro(
            id = itemId.toInt(),
            preco = preco,
            bateria = bateria,
            potencia = potencia,
            recarga = recarga,
            urlPhoto = urlPhoto,
            isFavorite = true
        )
    }

    fun saveIfNotExist(carro: Carro) {
        val car = findCarById(carro.id)
        if (car.id == ID_WHEN_NO_CAR) {
            save(carro)
        }
    }

    fun getAll(): List<Carro> {
        val dbHelper = CarsDbHelper(context)
        val db = dbHelper.readableDatabase

        //Listagem das colunas a serem exibidas no resultado da Query
        val columns = arrayOf(
            BaseColumns._ID,
            COLLUMN_NAME_CAR_ID,
            COLLUMN_NAME_PRECO,
            COLLUMN_NAME_BATERIA,
            COLLUMN_NAME_POTENCIA,
            COLLUMN_NAME_RECARGA,
            COLLUMN_NAME_URL_PHOTO
        )

        val cursor = db.query(
            CarrosContract.CarEntry.TABLE_NAME, //nome da tabela
            columns, // as colunas a serem exibidas
            null, // where(filtro)
            null, // valor do where, substituindo o paramentro ?
            null,
            null,
            null
        )

        val carros = mutableListOf<Carro>()

        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(COLLUMN_NAME_CAR_ID))
                val preco = getString(getColumnIndexOrThrow(COLLUMN_NAME_PRECO))
                val bateria = getString(getColumnIndexOrThrow(COLLUMN_NAME_BATERIA))
                val potencia = getString(getColumnIndexOrThrow(COLLUMN_NAME_POTENCIA))
                val recarga = getString(getColumnIndexOrThrow(COLLUMN_NAME_RECARGA))
                val urlPhoto = getString(getColumnIndexOrThrow(COLLUMN_NAME_URL_PHOTO))
                carros.add(
                    Carro(
                        id = itemId.toInt(),
                        preco = preco,
                        bateria = bateria,
                        potencia = potencia,
                        recarga = recarga,
                        urlPhoto = urlPhoto,
                        isFavorite = true
                    )
                )

            }
        }
        cursor.close()
        return carros

    }



    companion object {
        const val ID_WHEN_NO_CAR = 0
    }
}