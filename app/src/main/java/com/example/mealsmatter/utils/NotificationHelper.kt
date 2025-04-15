package com.example.mealsmatter.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.mealsmatter.R

// Utility class for displaying notifications
class NotificationHelper(private val context: Context) {
    companion object {
        const val CHANNEL_ID = "meal_reminder_channel" // Unique channel ID
        const val CHANNEL_NAME = "Meal Reminders"      // Channel name visible to users
        const val NOTIFICATION_ID = 1                  // Static notification ID
    }

    // Create the notifications channel as soon as this helper is instantiated
    init {
        createNotificationChannel()
    }

    // Sets up the channel
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for meal reminder notifications"
            }
            
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Builds and displays the meal reminder notification
    fun showNotification(mealName: String, time: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // App icon
            .setContentTitle("Meal Reminder")
            .setContentText("Time for $mealName at $time")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Dismiss when tapped

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
} 