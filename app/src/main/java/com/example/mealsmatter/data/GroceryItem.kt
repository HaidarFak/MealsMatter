package com.example.mealsmatter.data

data class GroceryItem(
    var id: Long = 0L,
    var name: String,
    var quantity: String = "",
    var isChecked: Boolean = false
)
