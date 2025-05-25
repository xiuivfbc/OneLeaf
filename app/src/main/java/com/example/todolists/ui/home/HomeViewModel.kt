package com.example.todolists.ui.home

import androidx.lifecycle.ViewModel
import com.example.todolists.data.ToDoListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    private val toDoListRepository: ToDoListRepository
): ViewModel() {
    private val _repositories = MutableStateFlow<List<String>>(emptyList())
    val repositories: StateFlow<List<String>> = _repositories.asStateFlow()

    init {
        loadRepositories()
    }

    private fun loadRepositories() {
        _repositories.value = toDoListRepository.getAllRepositories()
    }
}
