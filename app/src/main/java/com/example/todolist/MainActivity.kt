package com.example.todolist

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.navigation.compose.rememberNavController
import com.example.todolist.ui.navigation.ToDoListsNavGraph
import com.example.todolist.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 请求所有需要的权限
        requestAllPermissions()

        setContent {
            AppTheme {
                val navController = rememberNavController()
                ToDoListsNavGraph(navController, modifier = Modifier)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestAllPermissions() {
        // 1. 请求通知权限（Android 13+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (!isGranted) {
                    Toast.makeText(this, "请授予通知权限以接收提醒", Toast.LENGTH_LONG).show()
                }
            }
            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // 2. 检查精确闹钟权限（Android 12+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(
                    this,
                    "请授予精确闹钟权限以保证提醒准时",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = "package:$packageName".toUri()
                }
                startActivity(intent)
            }
        }

        // 3. 检查通知渠道是否启用
        checkNotificationChannelStatus()
    }

    private fun checkNotificationChannelStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            val channel = notificationManager.getNotificationChannel("alarm_channel")
            if (channel?.importance == NotificationManager.IMPORTANCE_NONE) {
                // 引导用户开启通知渠道
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
                Toast.makeText(
                    this,
                    "请开启通知权限以便应用正常工作",
                    Toast.LENGTH_LONG
                ).show()
                startActivity(intent)
            }
        }
    }
}