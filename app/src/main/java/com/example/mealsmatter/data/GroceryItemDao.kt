package com.example.mealsmatter.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// Data Access Object interface for interacting with grocery_items table
@Dao
interface GroceryItemDao {
    // Returns all grocery items as a flow
    @Query("SELECT * FROM grocery_items ORDER BY isChecked ASC, name ASC")
    fun getAllItems(): Flow<List<GroceryItem>>
    // Inserts a new grocery item into the table
    @Insert
    suspend fun insertItem(item: GroceryItem)
    // Updates grocery items
    @Update
    suspend fun updateItem(item: GroceryItem)
    // Deletes grocery items
    @Delete
    suspend fun deleteItem(item: GroceryItem)
    // Deletes all grocery items that are marked as checked
    @Query("DELETE FROM grocery_items WHERE isChecked = 1")
    suspend fun deleteCheckedItems()
} 