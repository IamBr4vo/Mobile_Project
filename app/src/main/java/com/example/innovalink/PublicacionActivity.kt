package com.example.innovalink

import android.content.Intent
import android.os.Bundle
import android.widget.AbsListView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PublicacionActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var scrollPosition = 0 // Variable para guardar la posición de desplazamiento
    private lateinit var adapter: CustomAdapter // Adaptador global para la lista de proyectos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.publication)

        val userId = intent.getIntExtra("user_id", -1)
        val userName = intent.getStringExtra("user_name") ?: "Usuario"
        if (userId == -1) {
            Toast.makeText(this, "Error al cargar el usuario", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val txtNameUser = findViewById<TextView>(R.id.txtNameUserDashboard)
        txtNameUser.text = userName

        val txtAddProject = findViewById<TextView>(R.id.txtGestion)
        txtAddProject.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("user_name", userName)
            intent.putExtra("isEditing", false)
            startActivityForResult(intent, 101) // Código de solicitud 101
        }

        dbHelper = DatabaseHelper(this)

        // Configurar el ListView para mantener la posición de desplazamiento
        val listView = findViewById<ListView>(R.id.listViewProjects)
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(
                view: AbsListView?,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                scrollPosition = firstVisibleItem
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                // No se requiere acción específica al cambiar el estado de desplazamiento
            }
        })

        // Cargar proyectos
        reloadProjects(userId, userName, listView)
    }

    private fun reloadProjects(userId: Int, userName: String, listView: ListView) {
        val projectList = dbHelper.getAllProjects().map { project ->
            project.copy(
                likes = dbHelper.getLikeCountForProject(project.id), // Contador actualizado de likes
                userHasLiked = dbHelper.userHasLikedProject(project.id, userId) // Estado de like del usuario
            )
        }.toMutableList() // Convertir a MutableList

        adapter = CustomAdapter(
            context = this,
            projectList = projectList,
            isEditable = false
        ) { projectId, isLiked ->
            dbHelper.toggleLike(projectId, userId) // Actualizar la base de datos
            reloadProjects(userId, userName, listView) // Recargar los proyectos después de cada acción
        }
        listView.adapter = adapter

        // Restaurar la posición de desplazamiento
        listView.setSelection(scrollPosition)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            val userId = intent.getIntExtra("user_id", -1)
            val userName = intent.getStringExtra("user_name") ?: "Usuario"
            val listView = findViewById<ListView>(R.id.listViewProjects)
            reloadProjects(userId, userName, listView)
        }
    }
}
