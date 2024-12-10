package com.example.innovalink

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide

class CustomAdapter(
    private val context: Context,
    private val projectList: List<Project>,
    private val isEditable: Boolean // Define si los proyectos pueden ser editados
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
        contentTextView.text = project.content // Contenido completo del proyecto

        // Cargar imagen del proyecto
        Glide.with(context)
            .load(project.imagePath)
            .placeholder(R.drawable.logo) // Imagen predeterminada
            .error(R.drawable.logo) // Imagen de error
            .into(imageView)

        // Configurar interacción


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
