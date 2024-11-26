package com.example.innovalink

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AddProjectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addproject)

        // Referencia al TextView txtAddProject
        val txtAddProject = findViewById<TextView>(R.id.btnExit2)

        // Configurar el evento de clic
        txtAddProject.setOnClickListener {
            // Cambiar el diseño al de addproject.xml
            setContentView(R.layout.dashboard)
        }

    }
}