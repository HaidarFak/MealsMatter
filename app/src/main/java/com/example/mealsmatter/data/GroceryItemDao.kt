package com.example.mealsmatter.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryItemDao {
    @Query("SELECT * FROM grocery_items ORDER BY isChecked ASC, name ASC")
    fun getAllItems(): Flow<List<GroceryItem>>

    @Insert
    suspend fun insertItem(item: GroceryItem)

    @Update
    suspend fun updateItem(item: GroceryItem)

    @Delete
    suspend fun deleteItem(item: GroceryItem)

    @Query("DELETE FROM grocery_items WHERE isChecked = 1")
    suspend fun deleteCheckedItems()
} 