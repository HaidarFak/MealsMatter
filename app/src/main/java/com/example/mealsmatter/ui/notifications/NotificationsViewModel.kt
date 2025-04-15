package com.example.mealsmatter.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// ViewModel for the NotificationsFragment
class NotificationsViewModel : ViewModel() {

    // Internal mutable LiveData
    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment" // Default message
    }

    // External immutable Livedata for the UI to observe
    val text: LiveData<String> = _text
}