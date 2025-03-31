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
import kotlinx.coroutines.launch
import com.example.mealsmatter.data.MealDatabase
import com.example.mealsmatter.data.Meal
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

        setupUpcomingMeals()
        setupButtons()
        setupDailyTip()
        checkNotificationPermission()
        setupCalendarButton()
    }

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

    private fun loadMealsForSelectedDate() {
        lifecycleScope.launch {
            val meals = if (selectedDate != null) {
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

    private fun setupUpcomingMeals() {
        binding.rvUpcomingMeals.layoutManager = LinearLayoutManager(context)
        loadMealsForSelectedDate()
    }

    private fun setupButtons() {
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_settings)
        }
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}