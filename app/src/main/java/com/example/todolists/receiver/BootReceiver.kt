package com.example.todolists.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todolists.ToDoListApplication
import com.example.todolists.utils.IconManager

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == "android.intent.action.QUICKBOOK_POWER") {
            
            // 初始化图标管理器并更新图标
            IconManager(context).updateAppIcon()
            
            // 重新设置每日定时任务
            (context.applicationContext as ToDoListApplication)
                .setupDailyIconUpdate()
        }
    }
}
