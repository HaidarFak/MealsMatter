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
import android.widget.EditText
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
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.launch

class MealPlanFragment : Fragment() {

    // Declare views
    private lateinit var etMealName: EditText
    private lateinit var btnPickDate: Button
    private lateinit var tvSelectedDate: TextView
    private lateinit var btnPickTime: Button
    private lateinit var tvSelectedTime: TextView
    private lateinit var etMealDescription: EditText
    private lateinit var btnSaveMeal: Button

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

    private fun scheduleMealReminder(mealName: String, calendar: Calendar) {
        val currentTime = Calendar.getInstance()
        val delayInMillis = calendar.timeInMillis - currentTime.timeInMillis
        
        if (delayInMillis <= 0) {
            Toast.makeText(requireContext(), "Please select a future date and time", Toast.LENGTH_SHORT).show()
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

        Toast.makeText(requireContext(), "Reminder set for $mealName", Toast.LENGTH_SHORT).show()
    }

    private fun saveMeal() {
        val mealName = etMealName.text.toString()
        val mealDate = tvSelectedDate.text.toString()
        val mealTime = tvSelectedTime.text.toString()
        val mealDescription = etMealDescription.text.toString()

        if (mealName.isEmpty() || mealDate == "No date selected" || mealTime == "No time selected" || mealDescription.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
        } else {
            // Schedule the notification
            scheduleMealReminder(mealName, selectedCalendar)

            // Save to database
            lifecycleScope.launch {
                val meal = Meal(
                    name = mealName,
                    description = mealDescription,
                    date = mealDate,
                    time = mealTime,
                    timestamp = selectedCalendar.timeInMillis
                )
                db.mealDao().insertMeal(meal)
            }

            // Save the meal (your existing toast)
            Toast.makeText(
                requireContext(),
                "Meal Saved: $mealName on $mealDate at $mealTime",
                Toast.LENGTH_SHORT
            ).show()

            // Clear the form
            etMealName.text.clear()
            tvSelectedDate.text = "No date selected"
            tvSelectedTime.text = "No time selected"
            etMealDescription.text.clear()
            
            // Reset the calendar
            selectedCalendar = Calendar.getInstance()
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
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }
}