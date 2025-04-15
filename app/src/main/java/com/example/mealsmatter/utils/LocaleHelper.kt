package com.example.mealsmatter.utils

import android.content.Context
import android.content.res.Configuration
import java.util.*

// Helper object for applying a new language/locale to the app context
object LocaleHelper {

    // Updates the app's context to use the selected language
    fun applyLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode) // Create a new locale object
        Locale.setDefault(locale) // Set it as the default for the app

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale) // Apply the new locale to the configuration

        // Return a new context with the updated configuration
        return context.createConfigurationContext(config)
    }
}
