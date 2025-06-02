package com.example.aula13

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DATABASE_NAME = "TodoDatabase"
private const val DATABASE_VERSION = 1
private const val TABLE_TASKS = "tasks"
private const val COLUMN_ID = "id"
private const val COLUMN_DESCRIPTION = "description"
private const val COLUMN_IS_COMPLETED = "is_completed"

class TaskDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val SQL_CREATE_ENTRIES =
        "CREATE TABLE $TABLE_TASKS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_DESCRIPTION TEXT NOT NULL," +
                "$COLUMN_IS_COMPLETED INTEGER NOT NULL DEFAULT 0)"

    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_TASKS"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    fun insertTask(description: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_IS_COMPLETED, 0)
        }
        return db.insert(TABLE_TASKS, null, values)
    }

    fun getAllTasks(): MutableList<Task> {
        val tasksList = mutableListOf<Task>()
        val db = this.readableDatabase
        val projection = arrayOf(COLUMN_ID, COLUMN_DESCRIPTION, COLUMN_IS_COMPLETED)
        val cursor = db.query(
            TABLE_TASKS,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val description = getString(getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                val isCompletedInt = getInt(getColumnIndexOrThrow(COLUMN_IS_COMPLETED))
                val isCompleted = isCompletedInt == 1
                tasksList.add(Task(id, description, isCompleted))
            }
        }
        cursor.close()
        return tasksList
    }

    fun updateTaskCompleted(id: Int, isCompleted: Boolean) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_COMPLETED, if (isCompleted) 1 else 0)
        }
        val selection = "$COLUMN_ID LIKE ?"
        val selectionArgs = arrayOf(id.toString())
        db.update(
            TABLE_TASKS,
            values,
            selection,
            selectionArgs
        )
    }

    fun deleteTask(id: Int) {
        val db = this.writableDatabase
        val selection = "$COLUMN_ID LIKE ?"
        val selectionArgs = arrayOf(id.toString())
        db.delete(
            TABLE_TASKS,
            selection,
            selectionArgs
        )
    }
}