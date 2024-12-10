package com.example.innovalink

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class AddProjectActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var imagePath: String? = null
    private var projectId: Int = -1
    private var isEditing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addproject)

        val userName = intent.getStringExtra("user_name") ?: "Usuario"
        val userId = intent.getIntExtra("user_id", -1)
        val userEmail = intent.getStringExtra("user_email") ?: "" // Obtenemos el Gmail del Intent

        val txtNameUserAdd = findViewById<TextView>(R.id.txtNameUserAdd)
        txtNameUserAdd.text = userName

        val btnExit = findViewById<Button>(R.id.btnExitAdd)
        val btnSearchImage = findViewById<Button>(R.id.btnSearchImage)
        val btnSave = findViewById<Button>(R.id.btnaddProject)
        val etName = findViewById<EditText>(R.id.txtNameProject)
        val etSubtitle = findViewById<EditText>(R.id.txtSubtitle)
        val etContent = findViewById<EditText>(R.id.txtContent)
        val etAuthor = findViewById<EditText>(R.id.txtAuthor)
        val etEmail = findViewById<EditText>(R.id.txtGmail) // Campo para Gmail
        imageView = findViewById(R.id.imageSelected)

        // Verificar si estamos en modo edición
        isEditing = intent.getBooleanExtra("isEditing", false)

        if (isEditing) {
            projectId = intent.getIntExtra("project_id", -1)
            if (projectId != -1) {
                loadProjectData(projectId, etName, etSubtitle, etContent, etAuthor, etEmail)
            } else {
                Toast.makeText(this, "Error al cargar el proyecto", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        // Buscar imagen en el dispositivo
        btnSearchImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

        // Guardar o actualizar proyecto
        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val subtitle = etSubtitle.text.toString()
            val content = etContent.text.toString()
            val author = etAuthor.text.toString()
            val email = etEmail.text.toString()

            if (name.isNotEmpty() && subtitle.isNotEmpty() && content.isNotEmpty() && author.isNotEmpty() && email.isNotEmpty()) {
                val dbHelper = DatabaseHelper(this)

                if (isEditing) {
                    // Actualizar proyecto existente
                    val success = dbHelper.updateProject(
                        projectId,
                        name,
                        subtitle,
                        content,
                        imagePath,
                        author,
                        email
                    )
                    if (success) {
                        Toast.makeText(this, "Proyecto actualizado exitosamente", Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, "Error al actualizar el proyecto", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Guardar nuevo proyecto
                    val success = dbHelper.insertProject(
                        userId,
                        name,
                        subtitle,
                        content,
                        imagePath,
                        author,
                        email
                    )
                    if (success) {
                        Toast.makeText(this, "Proyecto guardado exitosamente", Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, "Error al guardar el proyecto", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        val btnDelete = findViewById<Button>(R.id.btnDeleteProject)

        if (isEditing) {
            btnDelete.visibility = View.VISIBLE
            btnDelete.setOnClickListener {
                val builder = android.app.AlertDialog.Builder(this)
                builder.setTitle("Confirmación")
                builder.setMessage("¿Estás seguro de que deseas eliminar este proyecto? Esta acción no se puede deshacer.")
                builder.setPositiveButton("Sí") { _, _ ->
                    val dbHelper = DatabaseHelper(this)
                    val success = dbHelper.deleteProject(projectId)
                    if (success) {
                        Toast.makeText(this, "Proyecto eliminado", Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, "Error al eliminar el proyecto", Toast.LENGTH_SHORT).show()
                    }
                }
                builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                builder.create().show()
            }
        }

        // Salir
        btnExit.setOnClickListener { finish() }
    }

    private fun loadProjectData(
        projectId: Int,
        etName: EditText,
        etSubtitle: EditText,
        etContent: EditText,
        etAuthor: EditText,
        etEmail: EditText
    ) {
        val dbHelper = DatabaseHelper(this)
        val project = dbHelper.getProjectById(projectId)
        if (project != null) {
            etName.setText(project.name)
            etSubtitle.setText(project.subtitle)
            etContent.setText(project.content)
            etAuthor.setText(project.author)
            etEmail.setText(project.gmail) // Cargar Gmail en el campo correspondiente
            imagePath = project.imagePath
            if (!imagePath.isNullOrEmpty()) {
                try {
                    val file = File(imagePath)
                    if (file.exists()) {
                        imageView.setImageURI(Uri.fromFile(file))
                    } else {
                        Toast.makeText(this, "Imagen no encontrada", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                try {
                    val inputStream = contentResolver.openInputStream(selectedImageUri)
                    val file = File(filesDir, "project_image_${System.currentTimeMillis()}.jpg")
                    val outputStream = file.outputStream()

                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                    outputStream.close()

                    imagePath = file.absolutePath
                    imageView.setImageURI(Uri.fromFile(file))
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No se pudo seleccionar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
