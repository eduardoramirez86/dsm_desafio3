package com.example.desafio3dsm.network

import com.example.desafio3dsm.model.Recurso
import com.example.desafio3dsm.model.Usuario // <-- 1. Importa el nuevo modelo
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // --- API de Recursos (la que ya tenías) ---

    @GET("Recursos")
    fun getRecursos(): Call<List<Recurso>>

    @GET("Recursos/{id}")
    fun getRecursoById(@Path("id") id: String): Call<Recurso>

    @POST("Recursos")
    fun addRecurso(@Body recurso: Recurso): Call<Recurso>

    @PUT("Recursos/{id}")
    fun updateRecurso(@Path("id") id: String, @Body recurso: Recurso): Call<Recurso>

    @DELETE("Recursos/{id}")
    fun deleteRecurso(@Path("id") id: String): Call<Void>


    // --- 2. AGREGA ESTOS NUEVOS MÉTODOS ---

    /**
     * POST para registrar un nuevo usuario en el endpoint /Usuarios
     */
    @POST("Usuarios")
    fun registerUser(@Body usuario: Usuario): Call<Usuario>

    /**
     * GET para "loguear" a un usuario.
     * MockAPI no tiene un /login, así que usamos un filtro.
     * Esto busca en /Usuarios un registro que coincida EXACTAMENTE
     * con el email Y la contraseña.
     */
    @GET("Usuarios")
    fun getAllUsers(): Call<List<Usuario>> // Le quitamos los parámetros @Query
}