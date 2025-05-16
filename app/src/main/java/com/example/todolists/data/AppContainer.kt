package com.example.todolists.data

import android.content.Context

interface AppContainer {
    val toDoLisitRepository: ToDoListRepository
}

class DefaultAppContainer(
    private val context: Context
) : AppContainer {
    override val toDoLisitRepository: ToDoListRepository by lazy {
        ToDoListRepository(
            DatabaseManager(context)
        )
    }
}
