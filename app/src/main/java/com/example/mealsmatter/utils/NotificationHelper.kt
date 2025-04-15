package com.example.mealsmatter.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.mealsmatter.MainActivity
import com.example.mealsmatter.R

// Utility class for displaying notifications
object NotificationHelper {
    private const val CHANNEL_ID = "MEAL_REMINDER_CHANNEL"
    private const val NOTIFICATION_ID = 1

    fun showNotification(context: Context, mealName: String, mealTime: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Meal Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for meal reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent for when notification is tapped
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Meal Reminder")
            .setContentText("Time for $mealName at $mealTime")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Show notification
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
} 