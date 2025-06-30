package com.example.todolist.ui.item

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.ToDoItem
import com.example.todolist.data.ToDoListRepository
import com.example.todolist.alarm.AlarmReceiver
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

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val oldPendingIntent = PendingIntent.getBroadcast(
                context,
                item.id.toInt(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE // 检查是否存在旧闹钟
            )

            // 取消旧闹钟（如果存在）
            if (oldPendingIntent != null) {
                alarmManager.cancel(oldPendingIntent)
                oldPendingIntent.cancel()
                Log.d("ItemEditViewModel", "Old alarm canceled")
            }

            // 设置新闹钟（如果启用）
            if (item.enableAlarm && item.alarmTime != null) {
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

                val alarmTime = item.alarmTime!!.toInstant(ZoneOffset.UTC).toEpochMilli()
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    pendingIntent
                )
            }
        }
    }

    fun deleteItem(context: Context) {
        viewModelScope.launch {
            val item = _uiState.value.item
            repository.deleteItem(item, _uiState.value.repoId)

            // 取消关联的闹钟
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                item.id.toInt(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let {
                alarmManager.cancel(it)
                it.cancel()
                Log.d("ItemEditViewModel", "Alarm canceled for deleted item")
            }
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
