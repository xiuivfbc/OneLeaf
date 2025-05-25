package com.example.todolists.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todolists.ToDoListApplication
import com.example.todolists.ui.anyRepository.RepositoryViewModel
import com.example.todolists.ui.home.HomeViewModel
import com.example.todolists.ui.item.ItemEditViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                todolistApplication().container.toDoListRepository
            )
        }

        initializer {
            ItemEditViewModel(
                todolistApplication().container.toDoListRepository
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