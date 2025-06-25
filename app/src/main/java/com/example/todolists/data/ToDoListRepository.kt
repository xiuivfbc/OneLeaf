package com.example.todolists.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOn

class ToDoListRepository(private val databaseManager: DatabaseManager) {
    suspend fun insertItem(item: ToDoItem, name: String) {
        val db = databaseManager.getDatabase(name)
        db.todoItemDao().insert(item)
    }

    suspend fun createNewDatabase(name: String) {
        databaseManager.getDatabase(name)
    }

    fun getAllItems(name: String): Flow<List<ToDoItem>> {
        return kotlinx.coroutines.flow.flow {
            val db = databaseManager.getDatabase(name)
            emitAll(db.todoItemDao().getAllItems())
        }.flowOn(kotlinx.coroutines.Dispatchers.IO)
    }

    fun getItemById(id: Long, name: String): Flow<ToDoItem> {
        return kotlinx.coroutines.flow.flow {
            val db = databaseManager.getDatabase(name)
            emitAll(db.todoItemDao().getItem(id))
        }.flowOn(kotlinx.coroutines.Dispatchers.IO)
    }

    suspend fun updateItem(item: ToDoItem, name: String) {
        val db = databaseManager.getDatabase(name)
        db.todoItemDao().update(item)
    }

    suspend fun deleteItem(item: ToDoItem, name: String) {
        val db = databaseManager.getDatabase(name)
        db.todoItemDao().delete(item)
    }

    fun getAllRepositories(): Flow<List<String>> {
        return databaseManager.getAllDatabases()
    }

    suspend fun deleteRepository(name: String) {
        databaseManager.deleteDatabase(name)
    }
}
