package com.example.todolists.ui.item

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolists.data.ToDoItem
import com.example.todolists.data.ToDoListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ItemEditViewModel(
    private val toDoListRepository: ToDoListRepository
): ViewModel() {
    private lateinit var item_: MutableStateFlow<ToDoItem>
    var item: StateFlow<ToDoItem> = item_.asStateFlow()
    var name: String = ""
    var id: Long = 0

    fun init(name: String, id: Long) {
        this.name = name
        this.id = id
        viewModelScope.launch {
            toDoListRepository.getItemById(id, name).collect { item ->
                item_.value = item
            }
        }
    }

    fun updateItem(name: String, item: ToDoItem) {
        viewModelScope.launch {
            toDoListRepository.updateItem(item, name)
        }
    }

    fun deleteItem(name: String, item: ToDoItem) {
        viewModelScope.launch {
            toDoListRepository.deleteItem(item, name)
        }
    }
}