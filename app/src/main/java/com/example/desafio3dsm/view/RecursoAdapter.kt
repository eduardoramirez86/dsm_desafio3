package com.example.desafio3dsm.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.Disposable
import coil.request.ImageRequest
import coil.size.ViewSizeResolver
import com.example.desafio3dsm.databinding.ItemRecursoBinding
import com.example.desafio3dsm.model.Recurso
import okhttp3.OkHttpClient

class RecursoAdapter(
    private var recursos: List<Recurso>,
    private val onItemClicked: (Recurso) -> Unit,
    private val onItemLongClicked: (Recurso) -> Unit
) : RecyclerView.Adapter<RecursoAdapter.RecursoViewHolder>() {

    // Variable para guardar la instancia ÚNICA del ImageLoader.
    private var imageLoader: ImageLoader? = null

    /**
     * Obtiene una instancia única y cacheada del ImageLoader.
     * Si no existe, la crea. Si ya existe, la reutiliza.
     */
    private fun getCachedImageLoader(context: Context): ImageLoader {
        return imageLoader ?: ImageLoader.Builder(context)
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            }
            .components { add(SvgDecoder.Factory()) }
            .build().also {
                // Guardamos la instancia para futuras reutilizaciones.
                imageLoader = it
            }
    }

    inner class RecursoViewHolder(val binding: ItemRecursoBinding) : RecyclerView.ViewHolder(binding.root) {
        var imageRequestDisposable: Disposable? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecursoViewHolder {
        val binding = ItemRecursoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecursoViewHolder(binding)
    }

    override fun getItemCount(): Int = recursos.size

    override fun onBindViewHolder(holder: RecursoViewHolder, position: Int) {
        val recurso = recursos[position]

        holder.binding.tvTitulo.text = recurso.titulo
        holder.binding.tvTipo.text = "Tipo: ${recurso.tipo}"
        holder.binding.tvDescripcion.text = recurso.descripcion

        holder.imageRequestDisposable?.dispose()
        holder.imageRequestDisposable = null

        val context = holder.itemView.context
        val loader = getCachedImageLoader(context)

        val request = ImageRequest.Builder(context)
            .data(recurso.imagen)
            .crossfade(true)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_dialog_alert)
            .size(ViewSizeResolver(holder.binding.ivImagen))
            .target(holder.binding.ivImagen)
            .listener(onError = { _, result ->
                Log.e("RecursoAdapter", "Error cargando imagen: ${recurso.imagen}", result.throwable)
            })
            .build()

        holder.imageRequestDisposable = loader.enqueue(request)

        holder.itemView.setOnClickListener { onItemClicked(recurso) }

        holder.itemView.setOnLongClickListener {
            onItemLongClicked(recurso)
            true
        }

        holder.binding.btnAbrirEnlace.setOnClickListener {
            val enlaceUrl = recurso.enlace
            if (enlaceUrl.isNotBlank()) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(enlaceUrl))
                    holder.itemView.context.startActivity(intent)
                } catch (e: Exception) {
                    Log.w("RecursoAdapter", "No se pudo abrir enlace $enlaceUrl", e)
                    Toast.makeText(holder.itemView.context, "No se puede abrir el enlace", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(holder.itemView.context, "Este recurso no tiene enlace", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewRecycled(holder: RecursoViewHolder) {
        super.onViewRecycled(holder)
        holder.imageRequestDisposable?.dispose()
        holder.imageRequestDisposable = null
    }

    fun updateData(newRecursos: List<Recurso>) {
        recursos = newRecursos
        notifyDataSetChanged()
    }
}