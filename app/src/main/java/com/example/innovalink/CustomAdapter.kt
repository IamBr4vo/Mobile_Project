package com.example.innovalink

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
        val descriptionTextView = view.findViewById<TextView>(R.id.projectSubtitle)
        val authorTextView = view.findViewById<TextView>(R.id.projectAuthor) // TextView para el autor
        val imageView = view.findViewById<ImageView>(R.id.projectImage)

        titleTextView.text = project.name
        descriptionTextView.text = project.subtitle
        authorTextView.text = "Autor: ${project.author}" // Configurar el texto del autor

        Glide.with(context)
            .load(project.imagePath)
            .placeholder(R.drawable.logo) // Imagen predeterminada
            .error(R.drawable.logo) // Imagen de error
            .into(imageView)

        view.setOnClickListener {
            if (isEditable) {
                // Muestra un mensaje si es editable
                Toast.makeText(context, "Editando ${project.name}", Toast.LENGTH_SHORT).show()
            } else {
                // Muestra un mensaje si no es editable
                Toast.makeText(context, "Este proyecto no es editable.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
