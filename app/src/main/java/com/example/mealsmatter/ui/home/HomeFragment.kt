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
        val meals = listOf(
            UpcomingMeal("Breakfast", "8:00 AM", calories = 350),
            UpcomingMeal("Lunch", "12:30 PM", calories = 600),
            UpcomingMeal("Dinner", "7:00 PM", calories = 800)
        )

        val adapter = UpcomingMealsAdapter(meals) { meal ->
            // Handle meal click
            Toast.makeText(context, "Clicked: ${meal.name}", Toast.LENGTH_SHORT).show()
        }
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
                val upcomingMeals = meals.map { meal ->
                    UpcomingMeal(
                        name = meal.name,
                        time = meal.time,
                        calories = 0 // You might want to add calories to your Meal entity
                    )
                }
                (rvUpcomingMeals.adapter as UpcomingMealsAdapter).updateMeals(upcomingMeals)
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

    // Helper function to get a daily tip (dummy data for now)
    private fun getDailyTip(): String {
        return "Did you know? Eating breakfast boosts your metabolism!"
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
}