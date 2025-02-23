package com.example.mealsmatter.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.Data

class MealReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val mealName = inputData.getString("meal_name") ?: return Result.failure()
        val mealTime = inputData.getString("meal_time") ?: return Result.failure()

        // Show notification
        NotificationHelper(context).showNotification(mealName, mealTime)
        
        return Result.success()
    }
} 