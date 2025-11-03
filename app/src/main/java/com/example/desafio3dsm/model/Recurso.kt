package com.example.desafio3dsm.model

import com.google.gson.annotations.SerializedName

// Esta data class representa tu JSON
// Es como la clase Alumno [cite: 1272] o DogsResponse [cite: 488]
data class Recurso(
    @SerializedName("id") val id: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("enlace") val enlace: String,
    @SerializedName("imagen") val imagen: String
)