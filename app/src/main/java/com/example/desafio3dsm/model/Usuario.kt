package com.example.desafio3dsm.model

import com.google.gson.annotations.SerializedName

// Esta data class representa tu JSON de /Usuarios
data class Usuario(
    // El ID lo genera MockAPI, así que puede ser nulo al enviar
    @SerializedName("id") val id: String? = null,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)