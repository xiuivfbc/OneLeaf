package com.example.todolist

import android.app.Application
import androidx.work.Configuration
import com.example.todolist.data.AppContainer
import com.example.todolist.data.DefaultAppContainer

class ToDoListApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }

    fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }
}
