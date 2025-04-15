package com.example.mealsmatter.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Room database definition including Meal and GroceryItem entities
@Database(entities = [Meal::class, GroceryItem::class], version = 3)
abstract class MealDatabase : RoomDatabase() {
    // Access point for meal-related database operations
    abstract fun mealDao(): MealDao
    // Access point for grocery item-related operations
    abstract fun groceryItemDao(): GroceryItemDao

    companion object {
        // Singleton instance to prevent multiple database connections
        @Volatile
        private var INSTANCE: MealDatabase? = null

        // Migration from version 1 to 2: Adds grocery_items table
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the grocery_items table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS grocery_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        quantity TEXT NOT NULL,
                        isChecked INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
            }
        }

        // Migration from version 2 to 3: Recreates grocery_items for schema consistency
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add any new columns or modifications for version 3
                // For now, we're just recreating the tables to ensure schema consistency
                database.execSQL("DROP TABLE IF EXISTS grocery_items")
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS grocery_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        quantity TEXT NOT NULL,
                        isChecked INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
            }
        }

        // Returns the single instance of the database
        fun getDatabase(context: Context): MealDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MealDatabase::class.java,
                    "meal_database"
                )
                 // Adds support for migration
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                // Falls back to wiping the database if migration fails
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}