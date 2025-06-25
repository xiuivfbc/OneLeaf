package com.example.todolists.receiver

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todolists.R
import com.example.todolists.utils.IconManager

class AlarmReceiver : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Alarm triggered")
        
        // 更新应用图标
        IconManager(context).updateAppIcon()

        // 检查设备通知设置是否被禁用
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = notificationManager.getNotificationChannel("alarm_channel")
            if (channel?.importance == NotificationManager.IMPORTANCE_NONE) {
                Log.e("AlarmReceiver", "用户手动关闭了此渠道的通知")
                // 可以在这里跳转到应用通知设置页
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                return // 直接返回，不发送通知
            }
        }
        if (!notificationManager.areNotificationsEnabled()) {
            Log.e("AlarmReceiver", "全局通知被关闭")
            return
        }

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
