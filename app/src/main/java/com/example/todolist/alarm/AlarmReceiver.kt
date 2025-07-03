package com.example.todolist.alarm

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todolist.MainActivity
import com.example.todolist.R
import java.util.concurrent.TimeUnit
import kotlin.jvm.java

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        // 启动保活服务
        AlarmHolderService.start(context)

        val title = intent.getStringExtra("title") ?: "Reminder"
        val description = intent.getStringExtra("description") ?: ""
        val itemId = intent.getLongExtra("item_id", 0L)

        Log.d("AlarmReceiver", "Alarm triggered for item: $title (ID: $itemId)")

        // 1. 重新调度检查任务（避免重复）
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag("ALARM_CHECK")
        val alarmCheckRequest = OneTimeWorkRequestBuilder<AlarmCheckWorker>()
            .setInitialDelay(15, TimeUnit.MINUTES)
            .addTag("ALARM_CHECK")
            .build()
        workManager.enqueue(alarmCheckRequest)

        // 2. 创建通知渠道
        createNotificationChannel(context)

        // 3. 构建通知
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("item_id", itemId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            itemId.toInt(),
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .build()

        // 4. 发送通知（使用唯一 ID）
        NotificationManagerCompat.from(context).notify(itemId.toInt(), notification)

        // 15分钟后停止服务（如果不再需要）
        Handler(Looper.getMainLooper()).postDelayed({
            AlarmHolderService.stop(context)
        }, 15 * 60 * 1000L)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                enableVibration(true)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                    AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
                )
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }
}