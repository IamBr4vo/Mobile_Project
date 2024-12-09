package com.example.innovalink

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PublicacionActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.publication)

        // Obtener datos del usuario
        val userId = intent.getIntExtra("user_id", -1)
        val userName = intent.getStringExtra("user_name") ?: "Usuario"
        if (userId == -1) {
            Toast.makeText(this, "Error al cargar el usuario", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Mostrar nombre del usuario en el encabezado
        val txtNameUser = findViewById<TextView>(R.id.txtNameUserDashboard)
        txtNameUser.text = userName

        dbHelper = DatabaseHelper(this)

        // Cargar la lista de proyectos
        val listView = findViewById<ListView>(R.id.listViewProjects)
        val projectList = dbHelper.getAllProjects()

        val adapter = CustomAdapter(
            context = this,
            projectList = projectList,
            isEditable = false // Los proyectos no son editables en Publicación
        )
        listView.adapter = adapter

        // Configurar el botón para gestionar proyectos
        val txtGestion = findViewById<TextView>(R.id.txtGestion)
        txtGestion.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("user_name", userName)
            startActivity(intent)
        }
    }

    private fun reloadProjects() {
        val projectList = dbHelper.getAllProjects()
        val listView = findViewById<ListView>(R.id.listViewProjects)
        val adapter = CustomAdapter(
            context = this,
            projectList = projectList,
            isEditable = false // Los proyectos no son editables en Publicación
        )
        listView.adapter = adapter
    }
}
