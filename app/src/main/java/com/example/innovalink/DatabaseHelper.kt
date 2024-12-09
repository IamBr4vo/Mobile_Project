package com.example.innovalink

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDB"
        private const val DATABASE_VERSION = 3 // Incrementamos la versión de la base de datos
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_PASSWORD = "password"

        const val TABLE_PROJECTS = "projects"
        const val COLUMN_PROJECT_ID = "project_id"
        const val COLUMN_PROJECT_NAME = "project_name"
        const val COLUMN_PROJECT_SUBTITLE = "project_subtitle"
        const val COLUMN_PROJECT_CONTENT = "project_content"
        const val COLUMN_PROJECT_IMAGE_PATH = "project_image_path"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_PROJECT_AUTHOR = "author" // Nuevo atributo
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsersTableQuery = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL,
                $COLUMN_EMAIL TEXT UNIQUE NOT NULL,
                $COLUMN_PHONE TEXT NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """
        db?.execSQL(createUsersTableQuery)

        val createProjectsTableQuery = """
            CREATE TABLE $TABLE_PROJECTS (
                $COLUMN_PROJECT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PROJECT_NAME TEXT NOT NULL,
                $COLUMN_PROJECT_SUBTITLE TEXT NOT NULL,
                $COLUMN_PROJECT_CONTENT TEXT NOT NULL,
                $COLUMN_PROJECT_IMAGE_PATH TEXT,
                $COLUMN_USER_ID INTEGER NOT NULL,
                $COLUMN_PROJECT_AUTHOR TEXT NOT NULL,
                FOREIGN KEY ($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID)
            )
        """
        db?.execSQL(createProjectsTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            val addAuthorColumnQuery = "ALTER TABLE $TABLE_PROJECTS ADD COLUMN $COLUMN_PROJECT_AUTHOR TEXT NOT NULL DEFAULT ''"
            db?.execSQL(addAuthorColumnQuery)
        }
    }

    // Insertar usuario
    fun insertUser(username: String, email: String, phone: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USERNAME, username)
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PHONE, phone)
        values.put(COLUMN_PASSWORD, password)

        val result = db.insert(TABLE_USERS, null, values)
        return result != -1L
    }

    // Obtener usuario por correo y contraseña
    fun getUserByEmailAndPassword(email: String, password: String): User? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))

        var user: User? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
            user = User(id, username, email, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)))
        }
        cursor.close()
        return user
    }

    // Insertar proyecto
    fun insertProject(userId: Int, name: String, subtitle: String, content: String, imagePath: String?, author: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USER_ID, userId)
        values.put(COLUMN_PROJECT_NAME, name)
        values.put(COLUMN_PROJECT_SUBTITLE, subtitle)
        values.put(COLUMN_PROJECT_CONTENT, content)
        values.put(COLUMN_PROJECT_IMAGE_PATH, imagePath)
        values.put(COLUMN_PROJECT_AUTHOR, author)

        val result = db.insert(TABLE_PROJECTS, null, values)
        return result != -1L
    }

    // Obtener todos los proyectos
    fun getAllProjects(): List<Project> {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_PROJECTS"
        val cursor = db.rawQuery(query, null)
        val projects = mutableListOf<Project>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_NAME))
                val subtitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_SUBTITLE))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_CONTENT))
                val imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_IMAGE_PATH))
                val author = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_AUTHOR))
                projects.add(Project(id, name, subtitle, content, imagePath, author))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return projects
    }

    // Obtener proyectos por usuario
    fun getUserProjects(userId: Int): List<Project> {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_PROJECTS WHERE $COLUMN_USER_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))
        val projects = mutableListOf<Project>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_NAME))
                val subtitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_SUBTITLE))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_CONTENT))
                val imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_IMAGE_PATH))
                val author = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_AUTHOR))
                projects.add(Project(id, name, subtitle, content, imagePath, author))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return projects
    }

    // Obtener proyecto por ID
    fun getProjectById(projectId: Int): Project? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_PROJECTS WHERE $COLUMN_PROJECT_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(projectId.toString()))

        var project: Project? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_NAME))
            val subtitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_SUBTITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_CONTENT))
            val imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_IMAGE_PATH))
            val author = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_AUTHOR))
            project = Project(id, name, subtitle, content, imagePath, author)
        }
        cursor.close()
        return project
    }

    // Actualizar proyecto
    fun updateProject(projectId: Int, name: String, subtitle: String, content: String, imagePath: String?, author: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_PROJECT_NAME, name)
        values.put(COLUMN_PROJECT_SUBTITLE, subtitle)
        values.put(COLUMN_PROJECT_CONTENT, content)
        values.put(COLUMN_PROJECT_IMAGE_PATH, imagePath)
        values.put(COLUMN_PROJECT_AUTHOR, author)

        val result = db.update(TABLE_PROJECTS, values, "$COLUMN_PROJECT_ID = ?", arrayOf(projectId.toString()))
        return result > 0
    }

    // Eliminar proyecto
    fun deleteProject(projectId: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_PROJECTS, "$COLUMN_PROJECT_ID = ?", arrayOf(projectId.toString()))
        return result > 0
    }
}
