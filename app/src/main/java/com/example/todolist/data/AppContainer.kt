package com.example.todolist.data

import android.content.Context
import androidx.work.WorkManager

interface AppContainer {
    val toDoListRepository: ToDoListRepository
    val workManager: WorkManager
}

class DefaultAppContainer(
    private val context: Context
) : AppContainer {
    override val toDoListRepository: ToDoListRepository by lazy {
        val databaseManager = DatabaseManager(context)
        // Database will be initialized when first used
        ToDoListRepository(databaseManager)
    }

    override val workManager: WorkManager by lazy {
        WorkManager.getInstance(context)
    }

}
