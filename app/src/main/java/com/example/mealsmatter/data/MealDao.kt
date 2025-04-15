package com.example.mealsmatter.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// DAO interface for performing CRUD operations on meals table
@Dao
interface MealDao {
    // Retrieves all meals ordered chronologically by timestamp
    @Query("SELECT * FROM meals ORDER BY timestamp ASC")
    fun getAllMeals(): Flow<List<Meal>>

    // Gets meals scheduled for the future
    @Query("SELECT * FROM meals WHERE timestamp >= :currentTime AND isRecipe = 0 ORDER BY timestamp ASC")
    suspend fun getUpcomingMeals(currentTime: Long): List<Meal>
    // Gets meals for a specific date
    @Query("SELECT * FROM meals WHERE (date = :selectedDate OR date = :selectedDateAlt) AND isRecipe = 0 ORDER BY time ASC")
    suspend fun getMealsByDate(selectedDate: String, selectedDateAlt: String): List<Meal>
    // Inserts a new meal or recipe into the table
    @Insert
    suspend fun insertMeal(meal: Meal)
    // Deletes existing meal
    @Delete
    suspend fun deleteMeal(meal: Meal)
    // Updates an existing meal or recipe's details
    @Update
    suspend fun updateMeal(meal: Meal)
} 