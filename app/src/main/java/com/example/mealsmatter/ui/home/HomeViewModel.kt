package com.example.mealsmatter.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// ViewModel for the HomeFragment, Holds UI related data
class HomeViewModel : ViewModel() {

    // Backing property to allow modification internally
    private val _text = MutableLiveData<String>().apply {
        value = "Welcome to Meals Matter" // Initial welcome message
    }

    // Public read-only LiveData for observers
    val text: LiveData<String> = _text
}