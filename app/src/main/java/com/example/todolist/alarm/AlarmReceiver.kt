package com.example.todolist.alarm

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todolist.R

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Alarm triggered")

        val title = intent.getStringExtra("title") ?: ""
        var description = intent.getStringExtra("description") ?: ""

        // 创建通知渠道(Android 8.0+需要)
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
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                vibrationPattern =
                    longArrayOf(0, 500, 200, 500) // 振动模式：等待0ms，振动500ms，暂停200ms，振动500ms
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)) // 强制使用警报铃声
            .setVibrate(longArrayOf(0, 500, 200, 500)) // 与渠道振动模式一致
            .setCategory(NotificationCompat.CATEGORY_ALARM) // 关键！告诉系统这是闹钟类型
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // 锁屏可见
            .build()

        NotificationManagerCompat.from(context).notify(1, notification)
        Log.d("AlarmReceiver", "Notification sent")
    }
}
