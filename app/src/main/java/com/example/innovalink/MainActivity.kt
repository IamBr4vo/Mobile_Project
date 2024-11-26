package com.example.innovalink

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper // Instancia de la base de datos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = DatabaseHelper(this) // Inicializar la base de datos

        // Cargar el diseño inicial (login.xml)
        setContentView(R.layout.login)

        setupLoginView()
    }

    private fun setupLoginView() {
        val txtRegister = findViewById<TextView>(R.id.txtregister)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtEmail = findViewById<EditText>(R.id.txtEmailLogin)
        val txtPassword = findViewById<EditText>(R.id.txtPasswordLogin)

        // Configurar el evento para ir al registro
        txtRegister.setOnClickListener {
            setContentView(R.layout.register)
            setupRegisterView() // Configurar el layout de registro
        }

        // Configurar el evento de login
        btnLogin.setOnClickListener {
            val email = txtEmail.text.toString().trim()
            val password = txtPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Validar el usuario en la base de datos
                if (dbHelper.checkUser(email, password)) {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRegisterView() {
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnGoBack = findViewById<Button>(R.id.btnExit)
        val txtName = findViewById<EditText>(R.id.txtName)
        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtPhone = findViewById<EditText>(R.id.txtPhone)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)
        val txtPasswordConf = findViewById<EditText>(R.id.txtPasswordConf)

        // Configurar el evento de registro
        btnRegister.setOnClickListener {
            val name = txtName.text.toString().trim()
            val email = txtEmail.text.toString().trim()
            val phone = txtPhone.text.toString().trim()
            val password = txtPassword.text.toString()
            val confirmPassword = txtPasswordConf.text.toString()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {
                // Insertar usuario en la base de datos
                if (dbHelper.insertUser(name, email, phone, password)) {
                    Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                    setContentView(R.layout.login) // Volver al login
                    setupLoginView()
                } else {
                    Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Configurar el botón para volver al login
        btnGoBack.setOnClickListener {
            setContentView(R.layout.login)
            setupLoginView()
        }
    }
}
