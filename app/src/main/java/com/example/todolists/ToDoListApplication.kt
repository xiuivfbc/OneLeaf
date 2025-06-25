package com.example.todolists

import android.app.Application
import com.example.todolists.data.AppContainer
import com.example.todolists.data.DefaultAppContainer
import com.example.todolists.utils.IconManager
import com.example.todolists.worker.IconUpdateWorker

class ToDoListApplication : Application() {
    lateinit var container: AppContainer
    private lateinit var iconManager: IconManager

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        iconManager = IconManager(this)
        iconManager.updateAppIcon()
        setupDailyIconUpdate()
    }

    internal fun setupDailyIconUpdate() {
        val constraints = androidx.work.Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val periodicRequest = androidx.work.PeriodicWorkRequestBuilder<IconUpdateWorker>(
            1, java.util.concurrent.TimeUnit.DAYS
        )
            .setInitialDelay(calculateInitialDelay(), java.util.concurrent.TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()

        androidx.work.WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                IconUpdateWorker.WORK_NAME,
                androidx.work.ExistingPeriodicWorkPolicy.UPDATE,
                periodicRequest
            )
    }

    private fun calculateInitialDelay(): Long {
        val now = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = now
            add(java.util.Calendar.DAY_OF_YEAR, 1)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
        }
        return calendar.timeInMillis - now
    }
}
