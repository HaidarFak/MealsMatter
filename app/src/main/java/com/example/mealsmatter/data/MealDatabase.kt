package com.example.mealsmatter.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Meal::class, GroceryItem::class], version = 3)
abstract class MealDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun groceryItemDao(): GroceryItemDao

    companion object {
        @Volatile
        private var INSTANCE: MealDatabase? = null

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

        fun getDatabase(context: Context): MealDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MealDatabase::class.java,
                    "meal_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}