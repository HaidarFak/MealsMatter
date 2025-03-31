package com.example.mealsmatter.utils

import android.content.Context
import android.content.SharedPreferences

class SettingsManager private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    var language: String
        get() = prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    companion object {
        private const val PREFS_NAME = "MealReminders"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_LANGUAGE = "language"
        private const val DEFAULT_LANGUAGE = "en"
        
        @Volatile
        private var instance: SettingsManager? = null

        fun getInstance(context: Context): SettingsManager {
            return instance ?: synchronized(this) {
                instance ?: SettingsManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
