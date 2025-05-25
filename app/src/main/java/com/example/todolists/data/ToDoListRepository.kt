package com.example.todolists.data

import kotlinx.coroutines.flow.Flow

class ToDoListRepository(private val databaseManager: DatabaseManager) {
    private val defaultDatabaseName = DefaultAppContainer.DEFAULT_DATABASE_NAME

    suspend fun insertItem(item: ToDoItem, name: String = defaultDatabaseName) {
        val db = databaseManager.getDatabase(name)
        db.todoItemDao().insert(item)
    }

    fun getAllItems(name: String = defaultDatabaseName): Flow<List<ToDoItem>> {
        val db = databaseManager.getDatabase(name)
        return db.todoItemDao().getAllItems()
    }

    fun getItemById(id: Long, name: String = defaultDatabaseName): Flow<ToDoItem> {
        val db = databaseManager.getDatabase(name)
        return db.todoItemDao().getItem(id)
    }

    suspend fun updateItem(item: ToDoItem, name: String = defaultDatabaseName) {
        val db = databaseManager.getDatabase(name)
        db.todoItemDao().update(item)
    }

    suspend fun deleteItem(item: ToDoItem, name: String = defaultDatabaseName) {
        val db = databaseManager.getDatabase(name)
        db.todoItemDao().delete(item)
    }

    fun getAllRepositories(): List<String> {
        return databaseManager.getAllDatabases()
    }
}
