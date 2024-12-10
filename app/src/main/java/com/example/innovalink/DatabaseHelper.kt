package com.example.innovalink

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDB"
        private const val DATABASE_VERSION = 5 // Incrementado para incluir likes

        // Tabla de usuarios
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_PASSWORD = "password"

        // Tabla de proyectos
        const val TABLE_PROJECTS = "projects"
        const val COLUMN_PROJECT_ID = "project_id"
        const val COLUMN_PROJECT_NAME = "project_name"
        const val COLUMN_PROJECT_SUBTITLE = "project_subtitle"
        const val COLUMN_PROJECT_CONTENT = "project_content"
        const val COLUMN_PROJECT_IMAGE_PATH = "project_image_path"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_PROJECT_AUTHOR = "author"
        const val COLUMN_PROJECT_GMAIL = "gmail"

        // Tabla de likes
        const val TABLE_LIKES = "likes"
        const val COLUMN_LIKE_PROJECT_ID = "like_project_id"
        const val COLUMN_LIKE_USER_ID = "like_user_id"
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
                $COLUMN_PROJECT_GMAIL TEXT NOT NULL,
                FOREIGN KEY ($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID)
            )
        """
        db?.execSQL(createProjectsTableQuery)

        val createLikesTableQuery = """
            CREATE TABLE $TABLE_LIKES (
                $COLUMN_LIKE_PROJECT_ID INTEGER NOT NULL,
                $COLUMN_LIKE_USER_ID INTEGER NOT NULL,
                PRIMARY KEY ($COLUMN_LIKE_PROJECT_ID, $COLUMN_LIKE_USER_ID),
                FOREIGN KEY ($COLUMN_LIKE_PROJECT_ID) REFERENCES $TABLE_PROJECTS($COLUMN_PROJECT_ID),
                FOREIGN KEY ($COLUMN_LIKE_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID)
            )
        """
        db?.execSQL(createLikesTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 5) {
            val createLikesTableQuery = """
                CREATE TABLE IF NOT EXISTS $TABLE_LIKES (
                    $COLUMN_LIKE_PROJECT_ID INTEGER NOT NULL,
                    $COLUMN_LIKE_USER_ID INTEGER NOT NULL,
                    PRIMARY KEY ($COLUMN_LIKE_PROJECT_ID, $COLUMN_LIKE_USER_ID),
                    FOREIGN KEY ($COLUMN_LIKE_PROJECT_ID) REFERENCES $TABLE_PROJECTS($COLUMN_PROJECT_ID),
                    FOREIGN KEY ($COLUMN_LIKE_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID)
                )
            """
            db?.execSQL(createLikesTableQuery)
        }
    }

    fun insertUser(username: String, email: String, phone: String, password: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PHONE, phone)
            put(COLUMN_PASSWORD, password)
        }
        val result = db.insert(TABLE_USERS, null, values)
        return result != -1L
    }

    fun insertProject(
        userId: Int,
        name: String,
        subtitle: String,
        content: String,
        imagePath: String?,
        author: String,
        gmail: String
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_PROJECT_NAME, name)
            put(COLUMN_PROJECT_SUBTITLE, subtitle)
            put(COLUMN_PROJECT_CONTENT, content)
            put(COLUMN_PROJECT_IMAGE_PATH, imagePath)
            put(COLUMN_PROJECT_AUTHOR, author)
            put(COLUMN_PROJECT_GMAIL, gmail)
        }
        val result = db.insert(TABLE_PROJECTS, null, values)
        return result != -1L
    }
    fun updateProject(
        projectId: Int,
        name: String,
        subtitle: String,
        content: String,
        imagePath: String?,
        author: String,
        gmail: String
    ): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_PROJECT_NAME, name)
        values.put(COLUMN_PROJECT_SUBTITLE, subtitle)
        values.put(COLUMN_PROJECT_CONTENT, content)
        values.put(COLUMN_PROJECT_IMAGE_PATH, imagePath)
        values.put(COLUMN_PROJECT_AUTHOR, author)
        values.put(COLUMN_PROJECT_GMAIL, gmail)

        // Actualizar el proyecto por ID
        val result = db.update(TABLE_PROJECTS, values, "$COLUMN_PROJECT_ID = ?", arrayOf(projectId.toString()))
        return result > 0
    }

    fun getAllProjects(): List<Project> {
        val db = readableDatabase
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
                val gmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_GMAIL))
                projects.add(Project(id, name, subtitle, content, imagePath, author, gmail))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return projects
    }

    fun toggleLike(projectId: Int, userId: Int): Boolean {
        val db = writableDatabase
        val query = "SELECT * FROM $TABLE_LIKES WHERE $COLUMN_LIKE_PROJECT_ID = ? AND $COLUMN_LIKE_USER_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(projectId.toString(), userId.toString()))

        return if (cursor.moveToFirst()) {
            val result = db.delete(
                TABLE_LIKES,
                "$COLUMN_LIKE_PROJECT_ID = ? AND $COLUMN_LIKE_USER_ID = ?",
                arrayOf(projectId.toString(), userId.toString())
            )
            cursor.close()
            result > 0
        } else {
            val values = ContentValues().apply {
                put(COLUMN_LIKE_PROJECT_ID, projectId)
                put(COLUMN_LIKE_USER_ID, userId)
            }
            val result = db.insert(TABLE_LIKES, null, values)
            cursor.close()
            result != -1L
        }
    }

    fun getLikesForProject(projectId: Int): Int {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_LIKES WHERE $COLUMN_LIKE_PROJECT_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(projectId.toString()))

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }
    fun getUserProjects(userId: Int): List<Project> {
        val db = this.readableDatabase
        val query = """
        SELECT 
            p.$COLUMN_PROJECT_ID, 
            p.$COLUMN_PROJECT_NAME, 
            p.$COLUMN_PROJECT_SUBTITLE, 
            p.$COLUMN_PROJECT_CONTENT, 
            p.$COLUMN_PROJECT_IMAGE_PATH, 
            p.$COLUMN_PROJECT_AUTHOR, 
            p.$COLUMN_PROJECT_GMAIL,
            (SELECT COUNT(*) FROM $TABLE_LIKES WHERE $TABLE_LIKES.$COLUMN_LIKE_PROJECT_ID = p.$COLUMN_PROJECT_ID) AS likes
        FROM $TABLE_PROJECTS p
        WHERE p.$COLUMN_USER_ID = ?
    """
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
                val gmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_GMAIL))
                val likes = cursor.getInt(cursor.getColumnIndexOrThrow("likes"))

                projects.add(Project(id, name, subtitle, content, imagePath, author, gmail, likes))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return projects
    }
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
            val gmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_GMAIL))

            // Contar los likes del proyecto
            val likeCount = getLikesForProject(id)

            project = Project(id, name, subtitle, content, imagePath, author, gmail, likeCount)
        }
        cursor.close()
        return project
    }
    fun deleteProject(projectId: Int): Boolean {
        val db = this.writableDatabase

        // Eliminar likes asociados al proyecto
        db.delete(TABLE_LIKES, "$COLUMN_LIKE_PROJECT_ID = ?", arrayOf(projectId.toString()))

        // Eliminar el proyecto
        val result = db.delete(TABLE_PROJECTS, "$COLUMN_PROJECT_ID = ?", arrayOf(projectId.toString()))
        return result > 0
    }
    fun getUserByEmailAndPassword(email: String, password: String): User? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))

        var user: User? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE))
            user = User(id, username, email, phone)
        }
        cursor.close()
        return user
    }
    // Verificar si el usuario ya dio like a un proyecto
    fun userHasLikedProject(projectId: Int, userId: Int): Boolean {
        val db = this.readableDatabase
        val query = """
        SELECT * FROM $TABLE_LIKES 
        WHERE $COLUMN_LIKE_PROJECT_ID = ? AND $COLUMN_LIKE_USER_ID = ?
    """
        val cursor = db.rawQuery(query, arrayOf(projectId.toString(), userId.toString()))

        val hasLiked = cursor.moveToFirst()
        cursor.close()
        return hasLiked
    }// Obtener la cantidad total de likes de un proyecto
    // Obtener la cantidad total de likes de un proyecto
    fun getLikeCountForProject(projectId: Int): Int {
        val db = this.readableDatabase
        val query = """
        SELECT COUNT(*) FROM $TABLE_LIKES 
        WHERE $COLUMN_LIKE_PROJECT_ID = ?
    """
        val cursor = db.rawQuery(query, arrayOf(projectId.toString()))

        var likeCount = 0
        if (cursor.moveToFirst()) {
            likeCount = cursor.getInt(0) // Obtiene la primera columna con el conteo
        }
        cursor.close()
        return likeCount
    }




}
