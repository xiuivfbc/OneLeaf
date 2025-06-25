package com.example.todolists.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import java.util.Calendar

class IconManager(private val context: Context) {

    companion object {
        private val ICON_ALIASES = listOf(
            "com.example.todolists.MainActivity",
            "com.example.todolists.MainActivityAlias1",
            "com.example.todolists.MainActivityAlias2",
            "com.example.todolists.MainActivityAlias3"
        )
    }

    fun updateAppIcon() {
        val currentDay = getCurrentDayInCycle()
        val aliasToEnable = ICON_ALIASES[currentDay % ICON_ALIASES.size]

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
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        return dayOfYear % ICON_ALIASES.size
    }
}
