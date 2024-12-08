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
            private const val DATABASE_VERSION = 2
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
                FOREIGN KEY ($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID)
            )
        """
            db?.execSQL(createProjectsTableQuery)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_PROJECTS")
            onCreate(db)
        }

        // Insertar usuario en la base de datos
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
        fun insertProject(userId: Int, name: String, subtitle: String, content: String, imagePath: String?): Boolean {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(COLUMN_USER_ID, userId)
            values.put(COLUMN_PROJECT_NAME, name)
            values.put(COLUMN_PROJECT_SUBTITLE, subtitle)
            values.put(COLUMN_PROJECT_CONTENT, content)
            values.put(COLUMN_PROJECT_IMAGE_PATH, imagePath)

            val result = db.insert(TABLE_PROJECTS, null, values)
            return result != -1L
        }

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
                    projects.add(Project(id, name, subtitle, content, imagePath))
                } while (cursor.moveToNext())
            } else {
                Log.d("DatabaseHelper", "getUserProjects: No projects found for userId=$userId")
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
            project = Project(id, name, subtitle, content, imagePath)
        }
        cursor.close()
        return project
    }

    fun updateProject(projectId: Int, name: String, subtitle: String, content: String, imagePath: String?): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_PROJECT_NAME, name)
        values.put(COLUMN_PROJECT_SUBTITLE, subtitle)
        values.put(COLUMN_PROJECT_CONTENT, content)
        values.put(COLUMN_PROJECT_IMAGE_PATH, imagePath)

        val result = db.update(TABLE_PROJECTS, values, "$COLUMN_PROJECT_ID = ?", arrayOf(projectId.toString()))
        return result > 0
    }

    fun deleteProject(projectId: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_PROJECTS, "$COLUMN_PROJECT_ID = ?", arrayOf(projectId.toString()))
        return result > 0
    }
        }
