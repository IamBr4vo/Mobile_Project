package com.example.innovalink

    import android.content.ContentValues
    import android.content.Context
    import android.database.sqlite.SQLiteDatabase
    import android.database.sqlite.SQLiteOpenHelper

    class DatabaseHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val DATABASE_NAME = "UserDB"
            private const val DATABASE_VERSION = 1
            const val TABLE_USERS = "users"
            const val COLUMN_ID = "id"
            const val COLUMN_USERNAME = "username"
            const val COLUMN_EMAIL = "email"
            const val COLUMN_PHONE = "phone"
            const val COLUMN_PASSWORD = "password"
        }

        override fun onCreate(db: SQLiteDatabase?) {
            val createTableQuery = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL,
                $COLUMN_EMAIL TEXT UNIQUE NOT NULL,
                $COLUMN_PHONE TEXT NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """
            db?.execSQL(createTableQuery)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
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

        // Verificar si el usuario existe en la base de datos
        fun checkUser(email: String, password: String): Boolean {
            val db = this.readableDatabase
            val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
            val cursor = db.rawQuery(query, arrayOf(email, password))

            val exists = cursor.count > 0
            cursor.close()
            return exists
        }
    }