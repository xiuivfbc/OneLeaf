package com.example.todolists

import android.app.Application
import com.example.todolists.data.AppContainer
import com.example.todolists.data.DefaultAppContainer
import com.example.todolists.data.ToDoItem

class ToDoListApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}