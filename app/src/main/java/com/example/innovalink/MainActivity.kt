package com.example.innovalink

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var isInLoginView = true // Para rastrear el estado actual del layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cargar el diseño inicial (login.xml)
        setContentView(R.layout.login)
        setupLoginView()
    }

    private fun setupLoginView() {
        // Configurar el evento para ir a register.xml
        val txtRegister = findViewById<TextView>(R.id.txtregister)
        txtRegister.setOnClickListener {
            // Cambiar al diseño de register.xml
            setContentView(R.layout.register)
            setupRegisterView() // Configurar el layout de registro
        }
    }

    private fun setupRegisterView() {
        // Configurar el evento para volver a login.xml
        val btnGoBack = findViewById<Button>(R.id.btnExit)
        btnGoBack.setOnClickListener {
            // Cambiar de vuelta al diseño de login.xml
            setContentView(R.layout.login)
            setupLoginView() // Configurar el layout de login
        }
    }
}
