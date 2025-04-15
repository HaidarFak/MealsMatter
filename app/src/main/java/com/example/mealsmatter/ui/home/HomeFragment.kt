package com.example.mealsmatter.ui.home

import android.widget.Toast
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealsmatter.R
import com.example.mealsmatter.databinding.FragmentHomeBinding
import java.util.Calendar
import android.app.DatePickerDialog
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.mealsmatter.data.MealDatabase
import com.example.mealsmatter.api.FoodFactsApi
import androidx.navigation.fragment.findNavController
import java.text.SimpleDateFormat
import java.util.Locale
import android.util.Log

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: MealDatabase
    private val PERMISSION_REQUEST_CODE = 123
    private var selectedDate: String? = null
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = MealDatabase.getDatabase(requireContext())

        setupUpcomingMeals()    // Setup RecyclerView For Meals
        setupButtons()          // Setup settings button
        setupDailyTip()         // Show random wellness tip
        checkNotificationPermission() // Ask notification permissions
        setupCalendarButton()   // Calender date picker setup
    }

    // Handles calendar selection and date clearing
    private fun setupCalendarButton() {
        binding.btnCalendar.setOnClickListener {
            showDatePicker()
        }
        
        binding.btnClearDate.setOnClickListener {
            selectedDate = null
            binding.tvSelectedDate.text = ""
            binding.btnClearDate.visibility = View.GONE
            loadMealsForSelectedDate()
        }
    }

    // Opens date picker and filer meals based on date selection
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            R.style.CustomDatePickerDialog,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                selectedDate = dateFormat.format(calendar.time)
                binding.tvSelectedDate.text = "Selected: ${displayDateFormat.format(calendar.time)}"
                binding.btnClearDate.visibility = View.VISIBLE
                loadMealsForSelectedDate()
            },
            year,
            month,
            day
        ).show()
    }

    // Loads meals by selected date
    private fun loadMealsForSelectedDate() {
        lifecycleScope.launch {
            val meals = if (selectedDate != null) {
                // Handle alternative date formats
                Log.d("HomeFragment", "Selected date: $selectedDate")
                // Create alternative date format (single digit day)
                val dateParts = selectedDate!!.split("/")
                val day = dateParts[0].toInt()
                val month = dateParts[1]
                val year = dateParts[2]
                val selectedDateAlt = "$day/$month/$year"
                Log.d("HomeFragment", "Alternative date format: $selectedDateAlt")
                
                val mealsByDate = db.mealDao().getMealsByDate(selectedDate!!, selectedDateAlt)
                Log.d("HomeFragment", "Found ${mealsByDate.size} meals for date $selectedDate")
                mealsByDate.forEach { meal ->
                    Log.d("HomeFragment", "Meal: ${meal.name}, Date: ${meal.date}, Time: ${meal.time}, Timestamp: ${meal.timestamp}")
                }
                mealsByDate
            } else {
                val upcomingMeals = db.mealDao().getUpcomingMeals(System.currentTimeMillis())
                Log.d("HomeFragment", "Showing upcoming meals: ${upcomingMeals.size}")
                upcomingMeals.forEach { meal ->
                    Log.d("HomeFragment", "Upcoming Meal: ${meal.name}, Date: ${meal.date}, Time: ${meal.time}, Timestamp: ${meal.timestamp}")
                }
                upcomingMeals
            }

            // Adapter for displaying and handling meal actions
            val adapter = UpcomingMealsAdapter(
                meals = meals,
                onMealClick = { meal ->
                    Toast.makeText(context, "Clicked: ${meal.name}", Toast.LENGTH_SHORT).show()
                },
                onDeleteClick = { meal ->
                    lifecycleScope.launch {
                        db.mealDao().deleteMeal(meal)
                        loadMealsForSelectedDate() // Refresh the list
                    }
                },
                onEditClick = { meal, newName, newDescription, newDate, newTime ->
                    Log.d("HomeFragment", "Editing meal: ${meal.name}")
                    Log.d("HomeFragment", "Old date: ${meal.date}, New date: $newDate")
                    lifecycleScope.launch {
                        val updatedMeal = meal.copy(
                            name = newName,
                            description = newDescription,
                            date = newDate,
                            time = newTime
                        )
                        db.mealDao().updateMeal(updatedMeal)
                        loadMealsForSelectedDate() // Refresh the list
                    }
                }
            )
            binding.rvUpcomingMeals.adapter = adapter
        }
    }

    // Set up RecyclerView layout
    private fun setupUpcomingMeals() {
        binding.rvUpcomingMeals.layoutManager = LinearLayoutManager(context)
        loadMealsForSelectedDate()
    }

    // Handles navigation to the settings screen
    private fun setupButtons() {
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_settings)
        }
    }

    // Displays a random tip on the home screen
    private fun setupDailyTip() {
        val tips = listOf(
            "Meal prepping can save you time and money!",
            "Try to include a variety of colors in your meals for better nutrition.",
            "Don't forget to stay hydrated throughout the day!",
            "Including protein in your breakfast can help you feel fuller longer.",
            "Try to eat mindfully and avoid distractions while eating."
        )
        binding.tvDailyTip.text = tips.random()
    }

    // Request notification permissions on Android 13 and above
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }
    // Clean up view binding to prevent memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}