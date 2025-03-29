package com.example.mealsmatter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.mealsmatter.databinding.ActivityMainBinding
import com.example.mealsmatter.utils.LocaleHelper
import com.example.mealsmatter.utils.SettingsManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.NavigationUI

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsManager: SettingsManager

    override fun attachBaseContext(newBase: Context?) {
        settingsManager = SettingsManager.getInstance(newBase!!)
        val contextWithLocale = LocaleHelper.applyLocale(newBase, settingsManager.language)
        super.attachBaseContext(contextWithLocale)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply theme before layout
        settingsManager = SettingsManager.getInstance(this)
        val mode = if (settingsManager.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_meal_plan,
                R.id.navigation_notifications,
                R.id.navigation_settings
            )
        )

        // Set up the bottom navigation with the nav controller
        NavigationUI.setupWithNavController(navView, navController)

        // Add listener to handle home navigation
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home)
                    true
                }
                else -> NavigationUI.onNavDestinationSelected(item, navController)
            }
        }
    }
}
