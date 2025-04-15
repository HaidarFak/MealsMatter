package com.example.mealsmatter.ui.utils

import android.content.Context
import android.content.res.Configuration
import java.util.*

// Utility object to apply a specific locale to the app's context
object LocaleHelper {

    // Applies the given language code
    fun applyLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale) // Set as default for the app

        // Create a new configuration with the selected locale
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        // Return the context with the updated configuration
        return context.createConfigurationContext(config)
    }
}
