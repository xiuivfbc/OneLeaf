package com.example.todolists.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.todolists.utils.IconManager

class IconUpdateWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        IconManager(applicationContext).updateAppIcon()
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "icon_update_work"
    }
}
