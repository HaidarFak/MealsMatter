package com.example.mealsmatter.data

import androidx.room.Entity
import androidx.room.PrimaryKey
// Entity representing a meal or recipe stored in the "meals" Room database
@Entity(tableName = "meals")
data class Meal(
    // Auto generated unique ID for the meal entry
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    // Name of the meal
    val name: String,
    // Short description of meal
    val description: String,
    // Date of meal
    val date: String,
    // Time of meal
    val time: String,
    // Timestamps used for ordering meals
    val timestamp: Long,
    // Indicate whether this entry is a saved recipe or a planned meal
    val isRecipe: Boolean = false,
    // Ingredients list separated by a coma
    val ingredients: String = "",
    // Estimated cooking time
    val cookingTime: Int = 0,
    // Number of servings
    val servings: Int = 0,
    // Cooking/preparations instructions
    val instructions: String = ""
)