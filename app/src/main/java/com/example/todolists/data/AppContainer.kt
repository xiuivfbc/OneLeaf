package com.example.todolists.data

import android.content.Context

interface AppContainer {
    val toDoListRepository: ToDoListRepository
}

class DefaultAppContainer(
    private val context: Context
) : AppContainer {
    companion object {
        const val DEFAULT_DATABASE_NAME = "default_todo"
    }

    override val toDoListRepository: ToDoListRepository by lazy {
        val databaseManager = DatabaseManager(context)
        // Database will be initialized when first used
        ToDoListRepository(databaseManager)
    }
}
