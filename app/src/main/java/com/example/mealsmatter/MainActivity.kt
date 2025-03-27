package com.example.mealsmatter

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.mealsmatter.ui.utils.LocaleHelper
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        val prefs = newBase?.getSharedPreferences("MealReminders", MODE_PRIVATE)
        val langCode = prefs?.getString("language", "en") ?: "en"
        val contextWithLocale = LocaleHelper.applyLocale(newBase!!, langCode)
        super.attachBaseContext(contextWithLocale)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // ✅ Apply dark mode before layout
        val prefs = getSharedPreferences("MealReminders", MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_meal_plan,
                R.id.navigation_notifications
            )
        )

        navView.setupWithNavController(navController)
    }
}
