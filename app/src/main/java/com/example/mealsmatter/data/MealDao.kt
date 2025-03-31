package com.example.mealsmatter.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meals ORDER BY timestamp ASC")
    fun getAllMeals(): Flow<List<Meal>>

    @Query("SELECT * FROM meals WHERE timestamp >= :currentTime AND isRecipe = 0 ORDER BY timestamp ASC")
    suspend fun getUpcomingMeals(currentTime: Long): List<Meal>

    @Query("SELECT * FROM meals WHERE (date = :selectedDate OR date = :selectedDateAlt) AND isRecipe = 0 ORDER BY time ASC")
    suspend fun getMealsByDate(selectedDate: String, selectedDateAlt: String): List<Meal>

    @Insert
    suspend fun insertMeal(meal: Meal)

    @Delete
    suspend fun deleteMeal(meal: Meal)

    @Update
    suspend fun updateMeal(meal: Meal)
} 