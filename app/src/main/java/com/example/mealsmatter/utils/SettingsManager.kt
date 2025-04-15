package com.example.mealsmatter.utils

import android.content.Context
import android.content.SharedPreferences

// Singleton class to manage user settings like dark mode and language
class SettingsManager private constructor(context: Context) {
    // Access the shared preferences file
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Dark mode setting
    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    // App language setting
    var language: String
        get() = prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    // Notification setting
    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATIONS, value).apply()

    companion object {
        private const val PREFS_NAME = "MealReminders"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_NOTIFICATIONS = "notifications_enabled"
        private const val DEFAULT_LANGUAGE = "en"
        
        @Volatile
        private var instance: SettingsManager? = null

        // Thread-safe singleton getter
        fun getInstance(context: Context): SettingsManager {
            return instance ?: synchronized(this) {
                instance ?: SettingsManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
