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
        btnPlanMeal = root.findViewById(R.id.btn_plan_meal)
        btnViewGroceryList = root.findViewById(R.id.btn_view_grocery_list)
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

        // Set up button click listeners
        btnPlanMeal.setOnClickListener {
            // Navigate to the meal planning screen
            // Example: findNavController().navigate(R.id.action_homeFragment_to_mealPlanFragment)
            showToast("Navigate to Plan Meal Screen")
        }

        btnViewGroceryList.setOnClickListener {
            // Navigate to the grocery list screen
            // Example: findNavController().navigate(R.id.action_homeFragment_to_groceryListFragment)
            showToast("Navigate to Grocery List Screen")
        }

        // Set daily tip
        tvDailyTip.text = getDailyTip()

        // Observe ViewModel data (if needed)
        homeViewModel.text.observe(viewLifecycleOwner) {
            // Update UI with ViewModel data
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Request notification permission if needed
        requestNotificationPermission()
        
        binding.testNotificationButton.setOnClickListener {
            val calendar = Calendar.getInstance().apply {
                add(Calendar.MINUTE, 1) // Set notification for 1 minute from now
            }
            scheduleMealReminder("Test Meal", calendar)
        }
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