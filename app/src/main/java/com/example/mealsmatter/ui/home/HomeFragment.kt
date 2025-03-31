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

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: MealDatabase
    private val PERMISSION_REQUEST_CODE = 123

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

        setupGreeting()
        setupUpcomingMeals()
        setupButtons()
        setupDailyTip()
        checkNotificationPermission()
    }

    private fun setupGreeting() {
        val calendar = Calendar.getInstance()
        val greeting = when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
        binding.tvGreeting.text = greeting
    }

    private fun setupUpcomingMeals() {
        binding.rvUpcomingMeals.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch {
            val meals = db.mealDao().getUpcomingMeals(System.currentTimeMillis())
            val adapter = UpcomingMealsAdapter(
                meals = meals,
                onMealClick = { meal ->
                    Toast.makeText(context, "Clicked: ${meal.name}", Toast.LENGTH_SHORT).show()
                },
                onDeleteClick = { meal ->
                    lifecycleScope.launch {
                        db.mealDao().deleteMeal(meal)
                        setupUpcomingMeals() // Refresh the list
                    }
                },
                onEditClick = { meal, newName, newDescription, newDate, newTime ->
                    lifecycleScope.launch {
                        val updatedMeal = meal.copy(
                            name = newName,
                            description = newDescription,
                            date = newDate,
                            time = newTime
                        )
                        db.mealDao().updateMeal(updatedMeal)
                        setupUpcomingMeals() // Refresh the list
                    }
                }
            )
            binding.rvUpcomingMeals.adapter = adapter
        }
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