package com.example.eletriccarapp.data

import com.example.eletriccarapp.domain.Carro

object CarFactory {
    val list = listOf<Carro>(
        Carro(
            id = 1,
            preco = "R$ 300.000,00",
            bateria = "300kWh",
            potencia = "200cv",
            recarga = "30 min",
            urlPhoto = "www.google.com.br",
            isFavorite = false
        ),
        Carro(
            id = 2,
            preco = "R$ 300.000,00",
            bateria = "300kWh",
            potencia = "200cv",
            recarga = "30 min",
            urlPhoto = "www.google.com.br",
            isFavorite = false
        ),
        Carro(
            id = 3,
            preco = "R$ 300.000,00",
            bateria = "300kWh",
            potencia = "200cv",
            recarga = "30 min",
            urlPhoto = "www.google.com.br",
            isFavorite = false
        ),
        Carro(
            id = 4,
            preco = "R$ 300.000,00",
            bateria = "300kWh",
            potencia = "200cv",
            recarga = "30 min",
            urlPhoto = "www.google.com.br",
            isFavorite = false
        )

    )
}