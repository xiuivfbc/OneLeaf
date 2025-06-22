package com.example.todolists.ui.anyRepository

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolists.data.ToDoItem
import com.example.todolists.data.ToDoListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class RepositoryViewModel(
    private val toDoListRepository: ToDoListRepository
) : ViewModel() {
    private val items_ = MutableStateFlow<List<ToDoItem>>(emptyList())
    var items: StateFlow<List<ToDoItem>> = items_

    fun loadItems(dbName: String) {
        viewModelScope.launch {
            toDoListRepository.getAllItems(dbName).collect { list ->
                items_.value = list
            }
        }
    }

    fun addItem(dbName: String, title: String, describe: String = "", time: Long = 0, dateTime: LocalDateTime? = null) {
        viewModelScope.launch {
            val newItem = ToDoItem(
                title = title, 
                describe = describe, 
                time = time,
                dateTime = dateTime
            )
            toDoListRepository.insertItem(newItem, dbName)
            loadItems(dbName)
        }
    }

    fun deleteRepository(dbName: String) {
        viewModelScope.launch {
            toDoListRepository.deleteRepository(dbName)
        }
    }
}
