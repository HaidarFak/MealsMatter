package com.example.mealsmatter

import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val showCalendarButton: Button = findViewById(R.id.showCalendarButton)
        val calendarView: CalendarView = findViewById(R.id.calendarView)

        // Initially hide the calendar
        calendarView.visibility = CalendarView.GONE

        // Show or hide the calendar when the button is clicked
        showCalendarButton.setOnClickListener {
            if (calendarView.visibility == CalendarView.GONE) {
                calendarView.visibility = CalendarView.VISIBLE
            } else {
                calendarView.visibility = CalendarView.GONE
            }
        }

        // Set listener for date change (Built-in CalendarView)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Show a toast with the selected date
            val dateString = "$dayOfMonth/${month + 1}/$year"
            Toast.makeText(this, "Selected Date: $dateString", Toast.LENGTH_SHORT).show()
        }
    }
}
