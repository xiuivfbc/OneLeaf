package com.example.todolists.data

import androidx.room.Room
import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.Flow

class DatabaseManager(private val context: Context) {
    private companion object {
        const val SP_KEY = "database_names"
    }

    private val sharedPrefs = context.getSharedPreferences("database_manager", Context.MODE_PRIVATE)
    private val database = mutableMapOf<String, ToDoItemDatabase>()

    fun getDatabase(name: String): ToDoItemDatabase {
        return database[name] ?: createNewDatabase(name).also {
            database[name] = it
        }
    }

    fun getAllDatabases(): Flow<List<String>> {
        return kotlinx.coroutines.flow.flow {
            val names = sharedPrefs.getStringSet(SP_KEY, emptySet())?.toList() ?: emptyList()
            emit(names)
        }
    }

    private fun createNewDatabase(name: String): ToDoItemDatabase {
        sharedPrefs.edit {
            val currentNames = sharedPrefs.getStringSet(SP_KEY, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
            currentNames.add(name)
            putStringSet(SP_KEY, currentNames)
        }
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