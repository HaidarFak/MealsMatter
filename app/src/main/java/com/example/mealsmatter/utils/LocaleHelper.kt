package com.example.mealsmatter.utils

import android.content.Context
import android.content.res.Configuration
import java.util.*

object LocaleHelper {
    fun applyLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        
        return context.createConfigurationContext(config)
    }
}
