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
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todolist.alarm.AlarmCheckWorker
import com.example.todolist.alarm.AlarmHolderService
import com.example.todolist.alarm.AlarmReceiver
import com.example.todolist.data.ToDoItem
import com.example.todolist.data.ToDoListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

class ItemEditViewModel(
    private val repository: ToDoListRepository,
    private val workManager: WorkManager
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

            // 启动保活服务
            AlarmHolderService.start(context)

            // 取消旧闹钟（如果存在）
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val oldPendingIntent = PendingIntent.getBroadcast(
                context,
                item.id.toInt(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (oldPendingIntent != null) {
                alarmManager.cancel(oldPendingIntent)
                oldPendingIntent.cancel()
                Log.d("ItemEditViewModel", "Old alarm canceled")
            }

            // 设置WorkManager定期检查
            if (item.enableAlarm && item.alarmTime != null) {
                setExactAlarm(context, item)

                val alarmCheckRequest = OneTimeWorkRequestBuilder<AlarmCheckWorker>()
                    .setInitialDelay(15, TimeUnit.MINUTES)
                    .build()

                workManager.enqueueUniqueWork(
                    "alarm_check_${item.id}",
                    ExistingWorkPolicy.REPLACE,
                    alarmCheckRequest
                )
                Log.d("ItemEditViewModel", "Scheduled alarm check work")
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

    // 使用AlarmManager设置精确闹钟
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ScheduleExactAlarm")
    private fun setExactAlarm(context: Context, item: ToDoItem) {
        val alarmTime =item.alarmTime!!.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli()
        val now = System.currentTimeMillis()
        Log.d("AlarmDebug", "尝试设置闹钟: ${item.title} 时间: ${alarmTime - now}ms后")

        // 如果闹钟时间在未来24小时内，立即设置
        if (alarmTime > now && alarmTime - now <= 24 * 60 * 60 * 1000) {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("title", item.title)
                putExtra("description", item.describe)
                putExtra("item_id", item.id)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                item.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(AlarmManager::class.java)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTime,
                pendingIntent
            )
            Log.d("AlarmDebug", "闹钟设置成功! PendingIntent: $pendingIntent")
        }
    }
}

data class ItemEditState(
    val repoId: String = "",
    val item: ToDoItem = ToDoItem(title = "", describe = ""),
    val isLoading: Boolean = false,
    val showDatePicker: Boolean = false,
    val selectedDateTime: LocalDateTime? = null
)
