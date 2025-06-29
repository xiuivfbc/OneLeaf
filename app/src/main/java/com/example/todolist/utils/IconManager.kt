package com.example.todolist.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

class IconManager(private val context: Context) {

    companion object {
        private val ICON_ALIASES = listOf(
            "com.example.todolist.MainActivity",
            "com.example.todolist.MainActivityAlias1",
            "com.example.todolist.MainActivityAlias2",
            "com.example.todolist.MainActivityAlias3"
        )
    }

    fun updateAppIcon() {
        val currentDay = getCurrentDayInCycle()
        val aliasToEnable = ICON_ALIASES[currentDay]

        ICON_ALIASES.forEach { alias ->
            val componentName = ComponentName(context.packageName, alias)
            val newState = if (alias == aliasToEnable) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }

            context.packageManager.setComponentEnabledSetting(
                componentName,
                newState,
                PackageManager.DONT_KILL_APP
            )
        }
    }

    private fun getCurrentDayInCycle(): Int {
        ICON_ALIASES.forEachIndexed { index, alias ->
            val componentName = ComponentName(context.packageName, alias)
            val state = context.packageManager.getComponentEnabledSetting(componentName)
            if (state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                return (index + 1) % ICON_ALIASES.size
            }
        }
        return 0
    }
}
