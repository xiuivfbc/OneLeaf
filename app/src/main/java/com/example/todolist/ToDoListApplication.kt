package com.example.todolist

import android.app.Application
import com.example.todolist.data.AppContainer
import com.example.todolist.data.DefaultAppContainer

class ToDoListApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
