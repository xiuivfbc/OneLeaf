package com.example.todolists.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolists.data.ToDoListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val toDoListRepository: ToDoListRepository
) : ViewModel() {
    private val _repositories = MutableStateFlow<List<String>>(emptyList())
    val repositories: StateFlow<List<String>> = _repositories.asStateFlow()

    init {
        loadRepositories()
    }

    private fun loadRepositories() {
        viewModelScope.launch {
            toDoListRepository.getAllRepositories()
                .stateIn(viewModelScope)
                .collect { name ->
                    _repositories.value = name
                }
        }
    }

    fun createNewRepository(name: String) {
        viewModelScope.launch {
            toDoListRepository.createNewDatabase(name)
            loadRepositories()
        }
    }
}
