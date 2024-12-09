package com.example.innovalink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var dbHelper: DatabaseHelper
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        // Obtener el user_id del Intent
        userId = intent.getIntExtra("user_id", -1)
        val userName = intent.getStringExtra("user_name") ?: "Usuario"
        if (userId == -1) {
            Toast.makeText(this, "Error al cargar el usuario", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val txtNameUser = findViewById<TextView>(R.id.txtNameUserDashboard)
        txtNameUser.text = userName

        dbHelper = DatabaseHelper(this)

        // Configurar el ListView y cargar proyectos
        val listView = findViewById<ListView>(R.id.listViewProjects)
        val projects = dbHelper.getUserProjects(userId)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, projects.map { it.name })
        listView.adapter = adapter

        // Manejar clic en elementos de la lista para editar
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedProject = dbHelper.getUserProjects(userId)[position]
            val intent = Intent(this, AddProjectActivity::class.java)
            intent.putExtra("isEditing", true)
            intent.putExtra("user_name", userName)
            intent.putExtra("project_id", selectedProject.id)
            startActivityForResult(intent, 101) // Código de solicitud 101
        }

        // Configurar el evento de clic para agregar proyectos
        val txtAddProject = findViewById<TextView>(R.id.txtGestion)
        txtAddProject.setOnClickListener {
            val intent = Intent(this, AddProjectActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("user_name", userName)
            intent.putExtra("isEditing", false)
            startActivityForResult(intent, 101) // Código de solicitud 101
        }
        val txtAtras = findViewById<TextView>(R.id.txtAtras)
        txtAtras.setOnClickListener {
            val intent = Intent(this, PublicacionActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("user_name", userName)
            intent.putExtra("isEditing", false)
            startActivityForResult(intent, 101) // Código de solicitud 101
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            reloadProjects()
        }
    }

    private fun reloadProjects() {
        val projects = dbHelper.getUserProjects(userId)
        adapter.clear()
        adapter.addAll(projects.map { it.name })
        adapter.notifyDataSetChanged()
    }
}
