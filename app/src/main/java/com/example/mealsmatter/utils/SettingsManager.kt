package com.example.mealsmatter.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class SettingsManager private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) {
            prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()
            updateTheme(value)
        }

    var isMealRemindersEnabled: Boolean
        get() = prefs.getBoolean(KEY_MEAL_REMINDERS, true)
        set(value) = prefs.edit().putBoolean(KEY_MEAL_REMINDERS, value).apply()

    var isGroceryRemindersEnabled: Boolean
        get() = prefs.getBoolean(KEY_GROCERY_REMINDERS, true)
        set(value) = prefs.edit().putBoolean(KEY_GROCERY_REMINDERS, value).apply()

    var language: String
        get() = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    private fun updateTheme(isDarkMode: Boolean) {
        val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    companion object {
        private const val PREFS_NAME = "MealReminders"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_MEAL_REMINDERS = "meal_reminders"
        private const val KEY_GROCERY_REMINDERS = "grocery_reminders"
        private const val KEY_LANGUAGE = "language"

        @Volatile
        private var instance: SettingsManager? = null

        fun getInstance(context: Context): SettingsManager {
            return instance ?: synchronized(this) {
                instance ?: SettingsManager(context.applicationContext).also { instance = it }
            }
        }
    }
} 