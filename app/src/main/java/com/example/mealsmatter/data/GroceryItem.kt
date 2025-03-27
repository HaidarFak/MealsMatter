package com.example.mealsmatter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grocery_items")
data class GroceryItem(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var name: String,
    var quantity: String = "",
    var isChecked: Boolean = false
)
