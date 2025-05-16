package com.example.todolists.data

import kotlinx.coroutines.flow.Flow

class ToDoListRepository(private val databaseManager: DatabaseManager) {
    suspend fun insertItem(name: String, item: ToDoItem) {
        val db = databaseManager.getDatabase(name)
        db.todoItemDao().insert(item)
    }

    suspend fun getAllItems(name: String): Flow<List<ToDoItem>> {
        val db = databaseManager.getDatabase(name)
        return db.todoItemDao().getAllItems()
    }

    suspend fun getItemById(name: String, id: Long): Flow<ToDoItem?> {
        val db = databaseManager.getDatabase(name)
        return db.todoItemDao().getItem(id)
    }

    suspend fun updateItem(name: String, item: ToDoItem) {
        val db = databaseManager.getDatabase(name)
        db.todoItemDao().update(item)
    }

    suspend fun deleteItem(name: String, item: ToDoItem) {
        val db = databaseManager.getDatabase(name)
        db.todoItemDao().delete(item)
    }
}
