package com.example.desafio3dsm.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.desafio3dsm.MainActivity
import com.example.desafio3dsm.databinding.ActivityLoginBinding
import com.example.desafio3dsm.model.Usuario
import com.example.desafio3dsm.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            handleLogin()
        }
        binding.tvGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleLogin() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        Log.d("LOGIN_DEBUG", "Intentando login con Email: [$email] y Pass: [$password]")

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email y contraseña requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        // --- 1. LLAMAMOS AL NUEVO MÉTODO ---
        val call = RetrofitClient.instance.getAllUsers() // Ya no pasamos email/pass aquí

        call.enqueue(object : Callback<List<Usuario>> {
            override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                if (response.isSuccessful) {
                    val todosLosUsuarios = response.body()

                    if (todosLosUsuarios != null) {
                        // --- 2. FILTRAMOS MANUALMENTE ---
                        // Buscamos en la lista un usuario que coincida
                        val matchingUser = todosLosUsuarios.find {
                            it.email == email && it.password == password
                        }

                        if (matchingUser != null) {
                            // ¡ENCONTRADO! Login exitoso.
                            Toast.makeText(this@LoginActivity, "¡Login exitoso!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // No se encontró en la lista
                            Toast.makeText(this@LoginActivity, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // La lista vino nula (raro, pero posible)
                        Toast.makeText(this@LoginActivity, "No se recibieron datos", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // El 404 que veíamos antes
                    val errorBody = response.errorBody()?.string()
                    Log.e("LOGIN_DEBUG", "Error en onResponse: ${response.code()} - $errorBody")
                    Toast.makeText(this@LoginActivity, "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                Log.e("LOGIN_DEBUG", "Error en onFailure: ${t.message}")
                Toast.makeText(this@LoginActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}