package com.example.mealsmatter

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.example.mealsmatter.databinding.ActivityMainBinding
import com.example.mealsmatter.utils.LocaleHelper
import com.example.mealsmatter.utils.SettingsManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.NavigationUI

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsManager: SettingsManager
    private lateinit var navController: NavController

    // Ensures locale is applied before UI loads
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
        navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Define top-level destinations for the app bar config
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_meal_plan,
                R.id.navigation_library,
                R.id.navigation_grocery
            )
        )

        // Set up the bottom navigation with the nav controller
        NavigationUI.setupWithNavController(navView, navController)
        
        // Set up destination changes listener
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Show/hide bottom navigation based on the destination
            when (destination.id) {
                R.id.navigation_settings -> {
                    binding.navView.visibility = android.view.View.VISIBLE
                }
                else -> {
                    binding.navView.visibility = android.view.View.VISIBLE
                }
            }
        }
        
        // Set up menu item click listener for direct navigation
        navView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    if (navController.currentDestination?.id != R.id.navigation_home) {
                        navController.navigate(R.id.navigation_home)
                    }
                    true
                }
                R.id.navigation_meal_plan -> {
                    if (navController.currentDestination?.id != R.id.navigation_meal_plan) {
                        navController.navigate(R.id.navigation_meal_plan)
                    }
                    true
                }
                R.id.navigation_library -> {
                    if (navController.currentDestination?.id != R.id.navigation_library) {
                        navController.navigate(R.id.navigation_library)
                    }
                    true
                }
                R.id.navigation_grocery -> {
                    if (navController.currentDestination?.id != R.id.navigation_grocery) {
                        navController.navigate(R.id.navigation_grocery)
                    }
                    true
                }
                else -> false
            }
        }
    }
}
