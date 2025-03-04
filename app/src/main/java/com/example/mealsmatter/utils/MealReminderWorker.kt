package com.example.mealsmatter.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.Data
import com.example.mealsmatter.R

class MealReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val mealName = inputData.getString("meal_name") ?: return Result.failure()
        val mealTime = inputData.getString("meal_time") ?: return Result.failure()

        showNotification(mealName, mealTime)
        return Result.success()
    }

    private fun showNotification(mealName: String, mealTime: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create notification channel for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Meal Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Build notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Make sure you have this icon in your drawable resources
            .setContentTitle("Meal Reminder")
            .setContentText("Time for $mealName at $mealTime")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "MEAL_REMINDER_CHANNEL"
        private const val NOTIFICATION_ID = 1
    }
} 