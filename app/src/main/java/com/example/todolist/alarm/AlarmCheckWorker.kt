package com.example.todolist.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todolist.data.ToDoItem
import com.example.todolist.data.ToDoListRepository
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class AlarmCheckWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: ToDoListRepository
) : CoroutineWorker(context, params) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        // 检查前确保服务运行
        AlarmHolderService.start(applicationContext)
        checkAndScheduleAlarms()
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun checkAndScheduleAlarms() {
        val now = Instant.now()
        repository.getUpcomingAlarms(now.plus(15, ChronoUnit.MINUTES)).collect { items ->
            items.forEach { item ->
                if (item.enableAlarm && item.alarmTime != null) {
                    val alarmTime = item.alarmTime!!.toInstant(ZoneOffset.UTC)
                    if (alarmTime.isAfter(now)) {
                        scheduleExactAlarm(item)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleExactAlarm(item: ToDoItem) {
        val alarmTime =item.alarmTime!!.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli()
        if (alarmTime == null) {
            Log.e("AlarmCheckWorker", "Alarm time is null for item ${item.title}")
            return
        }

        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(applicationContext, AlarmReceiver::class.java).apply {
            putExtra("title", item.title)
            putExtra("description", item.describe)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            item.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            pendingIntent
        )
        Log.d("AlarmCheckWorker", "Scheduled exact alarm for ${item.title} at $alarmTime")
    }

    companion object {
        class Factory(private val repository: ToDoListRepository) {
            fun create(context: Context, params: WorkerParameters): AlarmCheckWorker {
                return AlarmCheckWorker(context, params, repository)
            }
        }
    }
}