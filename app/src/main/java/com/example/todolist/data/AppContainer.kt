package com.example.todolist.data

import android.content.Context

interface AppContainer {
    val toDoListRepository: ToDoListRepository
}

class DefaultAppContainer(
    private val context: Context
) : AppContainer {
    override val toDoListRepository: ToDoListRepository by lazy {
        val databaseManager = DatabaseManager(context)
        // Database will be initialized when first used
        ToDoListRepository(databaseManager)
    }
}
