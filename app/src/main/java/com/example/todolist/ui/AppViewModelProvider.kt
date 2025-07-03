package com.example.todolist.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todolist.ToDoListApplication
import com.example.todolist.ui.anyRepository.RepositoryViewModel
import com.example.todolist.ui.home.HomeViewModel
import com.example.todolist.ui.item.ItemEditViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                todolistApplication().container.toDoListRepository
            )
        }

        initializer {
            ItemEditViewModel(
                todolistApplication().container.toDoListRepository,
                todolistApplication().container.workManager
            )
        }

        initializer {
            RepositoryViewModel(
                todolistApplication().container.toDoListRepository
            )
        }
    }
}

fun CreationExtras.todolistApplication(): ToDoListApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ToDoListApplication)