package com.example.mealsmatter

import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val calendarView: CalendarView = findViewById(R.id.calendarView)

        // Set listener for date change (Built-in CalendarView)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Show a toast with the selected date
            val dateString = "$dayOfMonth/${month + 1}/$year"
            Toast.makeText(this, "Selected Date: $dateString", Toast.LENGTH_SHORT).show()
        }
    }
}
