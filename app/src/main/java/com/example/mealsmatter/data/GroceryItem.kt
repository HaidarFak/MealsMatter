package com.example.mealsmatter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Represents a grocery item stored in the local Room database
// This entity maps to the grocery_items table
@Entity(tableName = "grocery_items")
data class GroceryItem(
    // Unique ID for each item, generated by Room
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    // Name of grocery item
    var name: String,
    // Optional quantity description
    var quantity: String = "",
    // Checking if the item is checked off
    var isChecked: Boolean = false
)
