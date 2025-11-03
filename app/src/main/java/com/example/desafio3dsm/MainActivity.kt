package com.example.desafio3dsm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.desafio3dsm.databinding.ActivityMainBinding
import com.example.desafio3dsm.model.Recurso
import com.example.desafio3dsm.network.RetrofitClient
import com.example.desafio3dsm.view.AddEditActivity
import com.example.desafio3dsm.view.RecursoAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: RecursoAdapter
    private var fullRecursosList: MutableList<Recurso> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hacemos el Toolbar oficial para que el menú aparezca
        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        binding.searchView.setOnQueryTextListener(this)
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddEditActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Se ejecuta cada vez que la pantalla vuelve a ser visible.
        loadRecursos()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sort_by_title -> {
                sortListByTitle()
                true
            }
            R.id.sort_by_type -> {
                sortListByType()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        adapter = RecursoAdapter(
            emptyList(),
            onItemClicked = { recurso ->
                val intent = Intent(this, AddEditActivity::class.java)
                intent.putExtra("RECURSO_ID", recurso.id)
                startActivity(intent)
            },
            onItemLongClicked = { recurso ->
                showDeleteConfirmation(recurso)
            }
        )
        binding.rvRecursos.layoutManager = LinearLayoutManager(this)
        binding.rvRecursos.adapter = adapter
    }

    private fun loadRecursos() {
        Toast.makeText(this, getString(R.string.cargando_recursos), Toast.LENGTH_SHORT).show()

        fullRecursosList.clear()
        adapter.updateData(emptyList())

        val call = RetrofitClient.instance.getRecursos()

        call.enqueue(object : Callback<List<Recurso>> {
            override fun onResponse(call: Call<List<Recurso>>, response: Response<List<Recurso>>) {
                if (response.isSuccessful) {
                    val recursos = response.body() ?: emptyList()
                    Log.d("MainActivity", "Datos recibidos de la API: ${recursos.size} items.")
                    fullRecursosList.addAll(recursos)
                    filterList(binding.searchView.query.toString())
                } else {
                    showError(getString(R.string.error_cargar_recursos) + ": ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Recurso>>, t: Throwable) {
                showError(t.message ?: getString(R.string.error_cargar_recursos))
                Log.e("MainActivity", "Fallo al cargar recursos", t)
            }
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        filterList(newText)
        return true
    }

    private fun filterList(query: String?) {
        val filteredList = if (query.isNullOrBlank()) {
            fullRecursosList
        } else {
            val queryLowerCase = query.lowercase(Locale.getDefault())
            fullRecursosList.filter { recurso ->
                recurso.titulo.lowercase(Locale.getDefault()).contains(queryLowerCase) ||
                        recurso.tipo.lowercase(Locale.getDefault()).contains(queryLowerCase)
            }
        }
        adapter.updateData(filteredList)
    }

    private fun sortListByTitle() {
        fullRecursosList.sortBy { it.titulo.lowercase(Locale.getDefault()) }
        filterList(binding.searchView.query.toString())
        Toast.makeText(this, "Lista ordenada por Título", Toast.LENGTH_SHORT).show()
    }

    private fun sortListByType() {
        fullRecursosList.sortBy { it.tipo.lowercase(Locale.getDefault()) }
        filterList(binding.searchView.query.toString())
        Toast.makeText(this, "Lista ordenada por Tipo", Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteConfirmation(recurso: Recurso) {
        AlertDialog.Builder(this)
            .setTitle(R.string.confirmar_eliminacion_titulo)
            .setMessage(getString(R.string.confirmar_eliminacion_mensaje))
            .setPositiveButton(R.string.si) { _, _ -> deleteRecurso(recurso.id) }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }

    private fun deleteRecurso(id: String) {
        val call = RetrofitClient.instance.deleteRecurso(id)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, getString(R.string.recurso_eliminado), Toast.LENGTH_SHORT).show()
                    loadRecursos()
                } else {
                    showError(getString(R.string.error_operacion))
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                showError(t.message ?: getString(R.string.error_operacion))
            }
        })
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
