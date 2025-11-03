package com.example.desafio3dsm.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.desafio3dsm.R
import com.example.desafio3dsm.databinding.ActivityAddEditBinding
import com.example.desafio3dsm.model.Recurso
import com.example.desafio3dsm.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditBinding
    private var currentResourceId: String? = null // Variable para saber si estamos en modo "Editar"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Revisa si se pasó un ID a esta Activity
        currentResourceId = intent.getStringExtra("RECURSO_ID")

        if (currentResourceId == null) {
            // Modo "Crear"
            supportActionBar?.title = getString(R.string.add_recurso)
        } else {
            // Modo "Editar"
            supportActionBar?.title = getString(R.string.edit_recurso)
            loadRecursoData(currentResourceId!!)
        }

        // 2. Configura el listener del botón Guardar
        binding.btnGuardar.setOnClickListener {
            saveRecurso()
        }
    }

    // Carga los datos del recurso si estamos en modo "Editar"
    private fun loadRecursoData(id: String) {
        val call = RetrofitClient.instance.getRecursoById(id)
        call.enqueue(object : Callback<Recurso> {
            override fun onResponse(call: Call<Recurso>, response: Response<Recurso>) {
                if (response.isSuccessful) {
                    val recurso = response.body()
                    recurso?.let {
                        // Rellena los campos del formulario [cite: 1438-1440]
                        binding.etTitulo.setText(it.titulo)
                        binding.etDescripcion.setText(it.descripcion)
                        binding.etTipo.setText(it.tipo)
                        binding.etEnlace.setText(it.enlace)
                        binding.etImagen.setText(it.imagen)
                    }
                } else {
                    showError(getString(R.string.error_operacion))
                }
            }

            override fun onFailure(call: Call<Recurso>, t: Throwable) {
                showError(t.message ?: getString(R.string.error_operacion))
            }
        })
    }

    // Valida y guarda (ya sea creando o actualizando)
    private fun saveRecurso() {
        val titulo = binding.etTitulo.text.toString()
        val descripcion = binding.etDescripcion.text.toString()
        val tipo = binding.etTipo.text.toString()
        val enlace = binding.etEnlace.text.toString()
        val imagen = binding.etImagen.text.toString()

        // Validación de campos (requerida por el desafío [cite: 52])
        if (titulo.isEmpty() || descripcion.isEmpty() || tipo.isEmpty() || enlace.isEmpty()) {
            Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // ID es "0" para MockAPI al crear, pero MockAPI lo ignora y genera uno nuevo.
        // Para editar, usamos el ID correcto.
        val idParaGuardar = currentResourceId ?: "0"

        val recurso = Recurso(
            id = idParaGuardar,
            titulo = titulo,
            descripcion = descripcion,
            tipo = tipo,
            enlace = enlace,
            imagen = imagen
        )

        if (currentResourceId == null) {
            // Modo "Crear" -> Llama a addRecurso
            createRecurso(recurso)
        } else {
            // Modo "Editar" -> Llama a updateRecurso
            updateRecurso(currentResourceId!!, recurso)
        }
    }

    private fun createRecurso(recurso: Recurso) {
        val call = RetrofitClient.instance.addRecurso(recurso)
        call.enqueue(object : Callback<Recurso> {
            override fun onResponse(call: Call<Recurso>, response: Response<Recurso>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddEditActivity, getString(R.string.recurso_agregado), Toast.LENGTH_SHORT).show()
                    finish() // Cierra esta activity y regresa a la lista
                } else {
                    showError(getString(R.string.error_operacion))
                }
            }

            override fun onFailure(call: Call<Recurso>, t: Throwable) {
                showError(t.message ?: getString(R.string.error_operacion))
            }
        })
    }

    private fun updateRecurso(id: String, recurso: Recurso) {
        val call = RetrofitClient.instance.updateRecurso(id, recurso)
        call.enqueue(object : Callback<Recurso> {
            override fun onResponse(call: Call<Recurso>, response: Response<Recurso>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddEditActivity, getString(R.string.recurso_actualizado), Toast.LENGTH_SHORT).show()
                    finish() // Cierra esta activity y regresa a la lista
                } else {
                    showError(getString(R.string.error_operacion))
                }
            }

            override fun onFailure(call: Call<Recurso>, t: Throwable) {
                showError(t.message ?: getString(R.string.error_operacion))
            }
        })
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}