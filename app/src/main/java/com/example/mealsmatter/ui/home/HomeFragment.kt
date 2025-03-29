package com.example.mealsmatter.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.mealsmatter.R
import com.example.mealsmatter.api.FoodFactsApi
import com.example.mealsmatter.data.Meal
import com.example.mealsmatter.data.MealDatabase
import com.example.mealsmatter.databinding.FragmentHomeBinding
import com.example.mealsmatter.utils.MealReminderWorker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Declare views
    private lateinit var tvGreeting: TextView
    private lateinit var rvUpcomingMeals: RecyclerView
    private lateinit var btnPlanMeal: Button
    private lateinit var btnViewGroceryList: Button
    private lateinit var tvDailyTip: TextView
    private lateinit var btnShowCalender:Button
    private lateinit var tvCurrentDateTime:TextView
    private var updatedDate: Date = Date()
    private lateinit var adapter: UpcomingMealsAdapter
    private val PERMISSION_REQUEST_CODE = 123
    private lateinit var db: MealDatabase


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
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
        btnShowCalender = root.findViewById(R.id.btnShowCalendar)
        tvCurrentDateTime = root.findViewById(R.id.tvCurrentDateTime)


        // Set up RecyclerView for upcoming meals
        rvUpcomingMeals.layoutManager = LinearLayoutManager(requireContext())

        updateDateTime(Date())

        btnShowCalender.setOnClickListener {
            showCalendarDialog()
        }
        
        adapter = UpcomingMealsAdapter(
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
//        lifecycleScope.launch {
//            db.mealDao().getAllMeals()
//                .collect { meals ->
//                    // Filter out recipes, only show planned meals
//
//                    val plannedMeals = meals.filter {
//                        val sdf = SimpleDateFormat("dd/M/yyyy")
//                        Log.d("MEALTAGG", "onCreateView: ${it.date} ${sdf.format(updatedDate)}")
//                        !it.isRecipe && it.date == sdf.format(updatedDate)
//                    }
//                    adapter.updateMeals(plannedMeals)
//                }
//        }

        loadMealsForDate(updatedDate,adapter)

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


    @SuppressLint("SetTextI18n")
    private fun updateDateTime(selectedDate: Date) {
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy hh:mm a", Locale.getDefault())
        val formattedDateTime = sdf.format(selectedDate)
        updatedDate = selectedDate
        tvCurrentDateTime.text = "Selected Date & Time:\n$formattedDateTime"
    }

    private fun showCalendarDialog() {
        val calendarView = CalendarView(requireContext())
        val builder = AlertDialog.Builder(requireContext())
            .setView(calendarView)
            .setTitle("Pick a Date")
            .setNegativeButton("Cancel", null)

        val dialog = builder.create()
        dialog.show()

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Optional: Reset time to something specific if needed
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)

            val selectedDate = calendar.time
            updateDateTime(selectedDate)
            loadMealsForDate(selectedDate, adapter = adapter )


            val formattedDateForBundle = "$dayOfMonth/${month + 1}/$year"
            val bundle = Bundle().apply {
                putString("selected_date", formattedDateForBundle)
            }

//            findNavController().navigate(R.id.navigation_meal_plan, bundle)
            dialog.dismiss()
        }
    }

    private fun loadMealsForDate(date: Date, adapter: UpcomingMealsAdapter) {
        lifecycleScope.launch {
            db.mealDao().getAllMeals()
                .collect { meals ->
                    val sdf = SimpleDateFormat("dd/M/yyyy", Locale.getDefault())
                    val plannedMeals = meals.filter {
                        !it.isRecipe && it.date == sdf.format(date)
                    }
                    adapter.updateMeals(plannedMeals)
                }
        }
    }


}