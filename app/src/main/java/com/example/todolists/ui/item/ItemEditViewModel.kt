package com.example.todolists.ui.item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolists.data.ToDoItem
import com.example.todolists.data.ToDoListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class ItemEditViewModel(
    private val repository: ToDoListRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ItemEditState())
    val uiState: StateFlow<ItemEditState> = _uiState.asStateFlow()

    fun init(repoId: String, itemId: Long) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val item = repository.getItemById(itemId, repoId).first()
                _uiState.value = ItemEditState(
                    repoId = repoId,
                    item = item,
                    isNew = item.id == 0L,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun saveItem(item: ToDoItem) {
        viewModelScope.launch {
            repository.updateItem(item, _uiState.value.repoId)
        }
    }

    fun deleteItem() {
        viewModelScope.launch {
            repository.deleteItem(_uiState.value.item, _uiState.value.repoId)
        }
    }
}

data class ItemEditState(
    val repoId: String = "",
    val item: ToDoItem = ToDoItem(title = "", describe = ""),
    val isNew: Boolean = true,
    val isLoading: Boolean = false
)
