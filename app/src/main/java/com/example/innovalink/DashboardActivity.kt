package com.example.innovalink

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        // Referencia al TextView txtAddProject
        val txtAddProject = findViewById<TextView>(R.id.txtAddProject)

        // Configurar el evento de clic
        txtAddProject.setOnClickListener {
            // Cambiar el diseño al de addproject.xml
            setContentView(R.layout.addproject)
        }

    }
}
