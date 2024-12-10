package com.example.innovalink

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide

class CustomAdapter(
    private val context: Context,
    val projectList: MutableList<Project>, // Cambiar a `val` para acceso público
    private val isEditable: Boolean,
    private val onLikeToggle: (projectId: Int, isLiked: Boolean) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = projectList.size

    override fun getItem(position: Int): Any = projectList[position]

    override fun getItemId(position: Int): Long = projectList[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.list_item_project, parent, false
        )

        val project = getItem(position) as Project

        // Configurar vistas del diseño
        val titleTextView = view.findViewById<TextView>(R.id.projectTitle)
        val subtitleTextView = view.findViewById<TextView>(R.id.projectSubtitle)
        val authorTextView = view.findViewById<TextView>(R.id.projectAuthor)
        val gmailTextView = view.findViewById<TextView>(R.id.projectEmail)
        val contentTextView = view.findViewById<TextView>(R.id.projectContent)
        val imageView = view.findViewById<ImageView>(R.id.projectImage)
        val likeIcon = view.findViewById<ImageView>(R.id.likeIcon)
        val likeCountTextView = view.findViewById<TextView>(R.id.projectLikes)

        // Setear valores
        titleTextView.text = project.name
        subtitleTextView.text = project.subtitle
        authorTextView.text = "Autor: ${project.author}"
        gmailTextView.text = "Gmail: ${project.gmail}"
        contentTextView.text = project.content

        // Cargar imagen del proyecto
        Glide.with(context)
            .load(project.imagePath)
            .placeholder(R.drawable.logo) // Imagen predeterminada
            .error(R.drawable.logo) // Imagen de error
            .into(imageView)

        // Actualizar la cantidad de likes
        likeCountTextView.text = "${project.likes} Likes"

        // Configurar estado del ícono de like


        // Acción de like
        likeIcon.setOnClickListener {
            val newLikedState = !project.userHasLiked
            onLikeToggle(project.id, newLikedState) // Notificar el cambio de estado
        }


        view.setOnClickListener {
            if (isEditable) {
                Toast.makeText(context, "Editando ${project.name}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Este proyecto no es editable.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
