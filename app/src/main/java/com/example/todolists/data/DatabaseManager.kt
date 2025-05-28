package com.example.todolists.data

import androidx.room.Room
import android.content.Context
import kotlinx.coroutines.flow.Flow

class DatabaseManager(private val context: Context) {
    private val database = mutableMapOf<String, ToDoItemDatabase>()

    fun getDatabase(name: String): ToDoItemDatabase {
        return database[name] ?: createNewDatabase(name).also {
            database[name] = it
        }
    }

    fun getAllDatabases(): Flow<List<String>> {
        return kotlinx.coroutines.flow.flow {
            emit(database.keys.toList())
        }
    }

    private fun createNewDatabase(name: String): ToDoItemDatabase {
        return Room.databaseBuilder(
            context,
            ToDoItemDatabase::class.java,
            "$name.db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    fun closeAll() {
        database.values.forEach { it.close() }
        database.clear()
    }
}
