package com.example.todolist.data

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class DatabaseManager(private val context: Context) {
    private val preferencesDataStore = PreferencesDataStore(context)
    private val database = mutableMapOf<String, ToDoItemDatabase>()

    suspend fun getDatabase(name: String): ToDoItemDatabase {
        return database[name] ?: createNewDatabase(name).also {
            database[name] = it
        }
    }

    fun getAllDatabases(): Flow<List<String>> {
        return preferencesDataStore.databaseNames
    }

    private suspend fun createNewDatabase(name: String): ToDoItemDatabase {
        val currentNames = preferencesDataStore.databaseNames.first().toMutableSet()
        currentNames.add(name)
        preferencesDataStore.saveDatabaseNames(currentNames)
        return Room.databaseBuilder(
            context,
            ToDoItemDatabase::class.java,
            "$name.db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    fun closeAll() {
        database.values.forEach { it.close() }
        database.clear()
    }

    suspend fun deleteDatabase(name: String) {
        val currentNames = preferencesDataStore.databaseNames.first().toMutableSet()
        currentNames.remove(name)
        preferencesDataStore.saveDatabaseNames(currentNames)

        database[name]?.close()
        database.remove(name)
        context.deleteDatabase("$name.db")
    }
}
