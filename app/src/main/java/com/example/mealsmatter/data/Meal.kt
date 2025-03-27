package com.example.mealsmatter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val date: String,
    val time: String,
    val timestamp: Long, // for sorting
    val isRecipe: Boolean = false, // flag to identify if this is a saved recipe
    val ingredients: String = "", // comma-separated list of ingredients
    val cookingTime: Int = 0, // in minutes
    val servings: Int = 0,
    val instructions: String = "" // step-by-step cooking instructions
)