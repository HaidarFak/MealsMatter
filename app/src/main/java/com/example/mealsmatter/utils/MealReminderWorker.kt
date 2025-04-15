package com.example.mealsmatter.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.mealsmatter.R

// Worker class triggered by workManager to show a meal reminder notification
class MealReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    // Called when work executes
    override fun doWork(): Result {
        val mealName = inputData.getString("meal_name") ?: return Result.failure()
        val mealTime = inputData.getString("meal_time") ?: return Result.failure()

        showNotification(mealName, mealTime) // Display notification
        return Result.success()
    }

    // Displays a system notification with the meal name and time
    private fun showNotification(mealName: String, mealTime: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create notification channel for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Meal Reminders", // Channel name show to users
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Build notification and configure
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Icon drawable
            .setContentTitle("Meal Reminder")
            .setContentText("Time for $mealName at $mealTime")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "MEAL_REMINDER_CHANNEL" // Unique ID for channel
        private const val NOTIFICATION_ID = 1 // ID used to post the notification
    }
} 