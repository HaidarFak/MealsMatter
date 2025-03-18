package com.example.mealsmatter.ui.home

import android.widget.Toast
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mealsmatter.R
import com.example.mealsmatter.databinding.FragmentHomeBinding
import com.example.mealsmatter.ui.home.UpcomingMeal
import androidx.work.*
import java.util.concurrent.TimeUnit
import java.util.Calendar
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mealsmatter.utils.MealReminderWorker
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.example.mealsmatter.data.MealDatabase
import com.example.mealsmatter.data.Meal
import com.example.mealsmatter.api.FoodFactsApi

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Declare views
    private lateinit var tvGreeting: TextView
    private lateinit var rvUpcomingMeals: RecyclerView
    private lateinit var btnPlanMeal: Button
    private lateinit var btnViewGroceryList: Button
    private lateinit var tvDailyTip: TextView

    private val PERMISSION_REQUEST_CODE = 123
    private lateinit var db: MealDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize views

        rvUpcomingMeals = root.findViewById(R.id.rv_upcoming_meals)
        tvDailyTip = root.findViewById(R.id.tv_daily_tip)

        // Set up RecyclerView for upcoming meals
        rvUpcomingMeals.layoutManager = LinearLayoutManager(requireContext())
        
        val adapter = UpcomingMealsAdapter(
            meals = emptyList(),
            onMealClick = { meal ->
                Toast.makeText(context, "Clicked: ${meal.name}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { meal ->
                deleteMeal(meal)
            },
            onEditClick = { meal, newName, newDescription, newDate, newTime ->
                updateMeal(meal, newName, newDescription, newDate, newTime)
            }
        )
        rvUpcomingMeals.adapter = adapter

        // Set daily tip
        tvDailyTip.text = getDailyTip()

        // Observe ViewModel data (if needed)
        homeViewModel.text.observe(viewLifecycleOwner) {
            // Update UI with ViewModel data
        }

        db = MealDatabase.getDatabase(requireContext())
        
        // Observe meals from database
        lifecycleScope.launch {
            db.mealDao().getAllMeals().collect { meals ->
                adapter.updateMeals(meals)
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Request notification permission if needed
        requestNotificationPermission()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Helper function to get a daily tip
    private fun getDailyTip(): String {
        // Start with default tip
        var tip = "Did you know? Eating breakfast boosts your metabolism!"
        
        // Launch a coroutine to fetch the tip
        lifecycleScope.launch {
            try {
                val newTip = FoodFactsApi.getRandomFoodFact()
                tvDailyTip.text = newTip
            } catch (e: Exception) {
                // Keep the default tip if there's an error
            }
        }
        
        return tip
    }

    private fun scheduleMealReminder(mealName: String, calendar: Calendar) {
        val currentTime = Calendar.getInstance()
        val delayInMillis = calendar.timeInMillis - currentTime.timeInMillis
        
        if (delayInMillis <= 0) {
            showToast("Please select a future date and time")
            return
        }

        // Create the input data for the worker
        val inputData = Data.Builder()
            .putString("meal_name", mealName)
            .putString("meal_time", "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}")
            .build()

        // Create the WorkRequest
        val reminderRequest = OneTimeWorkRequestBuilder<MealReminderWorker>()
            .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        // Schedule the work
        WorkManager.getInstance(requireContext())
            .enqueue(reminderRequest)

        showToast("Reminder set for $mealName")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && 
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showToast("Notification permission granted")
                } else {
                    showToast("Notification permission denied")
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun deleteMeal(meal: Meal) {
        lifecycleScope.launch {
            try {
                db.mealDao().deleteMeal(meal)
                showToast("Meal deleted successfully")
            } catch (e: Exception) {
                showToast("Error deleting meal")
            }
        }
    }

    private fun updateMeal(meal: Meal, newName: String, newDescription: String, newDate: String, newTime: String) {
        if (newName.isBlank()) {
            showToast("Meal name cannot be empty")
            return
        }

        lifecycleScope.launch {
            try {
                val updatedMeal = meal.copy(
                    name = newName,
                    description = newDescription,
                    date = newDate,
                    time = newTime,
                    timestamp = calculateTimestamp(newDate, newTime)
                )
                db.mealDao().updateMeal(updatedMeal)
                showToast("Meal updated successfully")
            } catch (e: Exception) {
                showToast("Error updating meal")
            }
        }
    }

    private fun calculateTimestamp(date: String, time: String): Long {
        val (day, month, year) = date.split("/").map { it.toInt() }
        val (hour, minute) = time.split(":").map { it.toInt() }
        
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1) // Calendar months are 0-based
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}