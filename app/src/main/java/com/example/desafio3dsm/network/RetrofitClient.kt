package com.example.desafio3dsm.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Objeto para inicializar Retrofit, como en la Guía #11 [cite: 592-595]
object RetrofitClient {

    // Tu URL base de MockAPI (sin el /Recursos)
    private const val BASE_URL = "https://6705fbc1031fd46a83119315.mockapi.io/api/DSM/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}