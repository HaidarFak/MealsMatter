package com.example.mealsmatter.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mealsmatter.R
import java.util.Calendar

class MealPlanFragment : Fragment() {

    // Declare views
    private lateinit var etMealName: EditText
    private lateinit var btnPickDate: Button
    private lateinit var tvSelectedDate: TextView
    private lateinit var btnPickTime: Button
    private lateinit var tvSelectedTime: TextView
    private lateinit var etMealDescription: EditText
    private lateinit var btnSaveMeal: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                tvSelectedTime.text = selectedTime
            },
            hour,
            minute,
            true // 24-hour format
        )
        timePickerDialog.show()
    }

    private fun saveMeal() {
        val mealName = etMealName.text.toString()
        val mealDate = tvSelectedDate.text.toString()
        val mealTime = tvSelectedTime.text.toString()
        val mealDescription = etMealDescription.text.toString()

        if (mealName.isEmpty() || mealDate == "No date selected" || mealTime == "No time selected" || mealDescription.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
        } else {
            // Save the meal (for now, just show a toast)
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
        }
    }
}