package com.example.todolist.ui.item

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.ToDoItem
import com.example.todolist.data.ToDoListRepository
import com.example.todolist.receiver.AlarmReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset

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
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    @RequiresApi(Build.VERSION_CODES.O)
    fun saveItem(item: ToDoItem, context: Context) {
        viewModelScope.launch {
            repository.updateItem(item, _uiState.value.repoId)

            if (item.enableAlarm && item.alarmTime != null) {
                // 检查精确闹钟权限
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    !alarmManager.canScheduleExactAlarms()
                ) {
                    Log.e("ItemEditViewModel", "缺少精确闹钟权限")
                    Toast.makeText(
                        context,
                        "请授予精确闹钟权限",
                        Toast.LENGTH_LONG
                    ).show()
                    return@launch
                }
                val intent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("title", item.title)
                    putExtra("description", item.describe)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    item.id.toInt(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val alarmTime =
                    item.alarmTime!!.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli()
                Log.d("ItemEditViewModel", "Setting alarm for item ${item.id} at ${item.alarmTime}")

                try {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        pendingIntent
                    )
                    Log.d("ItemEditViewModel", "Alarm set successfully")
                } catch (e: SecurityException) {
                    Log.e("ItemEditViewModel", "Failed to set alarm: ${e.message}")
                }
            }
        }
    }

    fun deleteItem() {
        viewModelScope.launch {
            repository.deleteItem(_uiState.value.item, _uiState.value.repoId)
        }
    }

    fun showDatePicker(show: Boolean) {
        _uiState.value = _uiState.value.copy(showDatePicker = show)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setSelectedDateTime(dateTime: LocalDateTime) {
        _uiState.value = _uiState.value.copy(
            selectedDateTime = dateTime,
            item = _uiState.value.item.copy(
                dateTime = dateTime,
                time = dateTime.toEpochSecond(ZoneOffset.UTC)
            )
        )
    }
}

data class ItemEditState(
    val repoId: String = "",
    val item: ToDoItem = ToDoItem(title = "", describe = ""),
    val isLoading: Boolean = false,
    val showDatePicker: Boolean = false,
    val selectedDateTime: LocalDateTime? = null
)
