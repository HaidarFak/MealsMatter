package com.example.mealsmatter.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.example.mealsmatter.R
import com.example.mealsmatter.data.Meal
import com.example.mealsmatter.data.MealDatabase
import com.example.mealsmatter.utils.MealReminderWorker
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.launch

class MealPlanFragment : Fragment() {

    // Declare views
    private lateinit var etMealName: TextInputEditText
    private lateinit var btnPickDate: Button
    private lateinit var tvSelectedDate: TextView
    private lateinit var btnPickTime: Button
    private lateinit var tvSelectedTime: TextView
    private lateinit var etMealDescription: TextInputEditText
    private lateinit var btnSaveMeal: Button

    // Recipe views
    private lateinit var switchIsRecipe: SwitchMaterial
    private lateinit var layoutRecipeDetails: ViewGroup
    private lateinit var etIngredients: TextInputEditText
    private lateinit var etCookingTime: TextInputEditText
    private lateinit var etServings: TextInputEditText
    private lateinit var etInstructions: TextInputEditText
    private lateinit var cbAddToSchedule: MaterialCheckBox

    private var selectedCalendar: Calendar = Calendar.getInstance()
    private lateinit var db: MealDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize database
        db = MealDatabase.getDatabase(requireContext())
        
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_meal_plan, container, false)

        // Initialize views
        etMealName = view.findViewById(R.id.et_meal_name)
        btnPickDate = view.findViewById(R.id.btn_pick_date)
        tvSelectedDate = view.findViewById(R.id.tv_selected_date)
        btnPickTime = view.findViewById(R.id.btn_pick_time)
        tvSelectedTime = view.findViewById(R.id.tv_selected_time)
        etMealDescription = view.findViewById(R.id.et_meal_description)
        btnSaveMeal = view.findViewById(R.id.btn_save_meal)

        // Initialize recipe views
        switchIsRecipe = view.findViewById(R.id.switch_is_recipe)
        layoutRecipeDetails = view.findViewById(R.id.layout_recipe_details)
        etIngredients = view.findViewById(R.id.et_ingredients)
        etCookingTime = view.findViewById(R.id.et_cooking_time)
        etServings = view.findViewById(R.id.et_servings)
        etInstructions = view.findViewById(R.id.et_instructions)
        cbAddToSchedule = view.findViewById(R.id.cb_add_to_schedule)

        // Check for recipe data in arguments
        arguments?.let { args ->
            if (args.containsKey("recipeName")) {
                // Pre-fill form with recipe data
                etMealName.setText(args.getString("recipeName"))
                etMealDescription.setText(args.getString("recipeDescription"))
                
                // Enable recipe mode and show recipe details
                switchIsRecipe.isChecked = true
                layoutRecipeDetails.visibility = View.VISIBLE
                
                // Fill recipe details
                etIngredients.setText(args.getString("recipeIngredients"))
                etCookingTime.setText(args.getInt("recipeCookingTime").toString())
                etServings.setText(args.getInt("recipeServings").toString())
                etInstructions.setText(args.getString("recipeInstructions"))
            }
        }

        // Set up recipe switch
        switchIsRecipe.setOnCheckedChangeListener { _, isChecked ->
            layoutRecipeDetails.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) {
                // Clear recipe fields when switching off
                etIngredients.text?.clear()
                etCookingTime.text?.clear()
                etServings.text?.clear()
                etInstructions.text?.clear()
                cbAddToSchedule.isChecked = true
            }
        }

        // Set up date picker
        btnPickDate.setOnClickListener {
            showDatePicker()
        }

        // Set up time picker
        btnPickTime.setOnClickListener {
            showTimePicker()
        }

        // Set up save button
        btnSaveMeal.setOnClickListener {
            saveMeal()
        }

        return view
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedCalendar.set(Calendar.YEAR, selectedYear)
                selectedCalendar.set(Calendar.MONTH, selectedMonth)
                selectedCalendar.set(Calendar.DAY_OF_MONTH, selectedDay)
                
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                tvSelectedDate.text = selectedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                selectedCalendar.set(Calendar.MINUTE, selectedMinute)
                
                val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                tvSelectedTime.text = selectedTime
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    private fun scheduleNotification(meal: Meal) {
        val currentTime = Calendar.getInstance()
        val delayInMillis = meal.timestamp - currentTime.timeInMillis
        
        if (delayInMillis <= 0) {
            Toast.makeText(requireContext(), "Please select a future date and time", Toast.LENGTH_SHORT).show()
            return
        }

        // Create the input data for the worker
        val inputData = Data.Builder()
            .putString("meal_name", meal.name)
            .putString("meal_time", "${meal.time}")
            .build()

        // Create the WorkRequest
        val reminderRequest = OneTimeWorkRequestBuilder<MealReminderWorker>()
            .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        // Schedule the work
        WorkManager.getInstance(requireContext())
            .enqueue(reminderRequest)

        Toast.makeText(requireContext(), "Reminder set for ${meal.name}", Toast.LENGTH_SHORT).show()
    }

    private fun saveMeal() {
        val name = etMealName.text.toString()
        val description = etMealDescription.text.toString()
        val isRecipe = switchIsRecipe.isChecked
        val addToSchedule = cbAddToSchedule.isChecked

        // Validate required fields based on mode
        if (name.isEmpty()) {
            Toast.makeText(context, "Please enter a meal name", Toast.LENGTH_SHORT).show()
            return
        }

        if (addToSchedule && (tvSelectedDate.text == "Select Date" || tvSelectedTime.text == "Select Time")) {
            Toast.makeText(context, "Please select date and time", Toast.LENGTH_SHORT).show()
            return
        }

        val ingredients = if (isRecipe) etIngredients.text.toString() else ""
        val cookingTime = if (isRecipe) etCookingTime.text.toString().toIntOrNull() ?: 0 else 0
        val servings = if (isRecipe) etServings.text.toString().toIntOrNull() ?: 0 else 0
        val instructions = if (isRecipe) etInstructions.text.toString() else ""

        // Check if we're using a recipe from library
        val isFromLibrary = arguments?.containsKey("recipeName") == true

        lifecycleScope.launch {
            // If this is a recipe and not from library, save it as a recipe
            if (isRecipe && !isFromLibrary) {
                val recipe = Meal(
                    name = name,
                    description = description,
                    date = "",  // Empty date for recipes
                    time = "",  // Empty time for recipes
                    timestamp = 0L,  // Zero timestamp for recipes
                    isRecipe = true,
                    ingredients = ingredients,
                    cookingTime = cookingTime,
                    servings = servings,
                    instructions = instructions
                )
                db.mealDao().insertMeal(recipe)
            }

            // Save the meal only if addToSchedule is checked
            if (addToSchedule) {
                val timestamp = selectedCalendar.timeInMillis
                val meal = Meal(
                    name = name,
                    description = description,
                    date = tvSelectedDate.text.toString(),
                    time = tvSelectedTime.text.toString(),
                    timestamp = timestamp,
                    isRecipe = false,  // Always false for planned meals
                    ingredients = ingredients,
                    cookingTime = cookingTime,
                    servings = servings,
                    instructions = instructions
                )
                db.mealDao().insertMeal(meal)

                // Schedule notification only for planned meals
                scheduleNotification(meal)
            }

            // Navigate back
            activity?.runOnUiThread {
                val message = when {
                    isRecipe && addToSchedule -> "Recipe saved and added to schedule"
                    isRecipe -> "Recipe saved to library"
                    else -> "Meal added to schedule"
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                activity?.onBackPressed()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Request notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    123
                )
            }
        }
    }
}