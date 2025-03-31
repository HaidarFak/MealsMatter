package com.example.mealsmatter.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mealsmatter.R
import com.example.mealsmatter.data.Meal
import com.example.mealsmatter.data.MealDatabase
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

class LibraryFragment : Fragment() {

    private lateinit var rvRecipes: RecyclerView
    private lateinit var tvEmptyLibrary: TextView
    private lateinit var adapter: RecipeAdapter
    private lateinit var db: MealDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)

        // Initialize views
        rvRecipes = view.findViewById(R.id.rv_recipes)
        tvEmptyLibrary = view.findViewById(R.id.tv_empty_library)

        // Initialize database
        db = MealDatabase.getDatabase(requireContext())

        // Set up RecyclerView
        adapter = RecipeAdapter(
            recipes = emptyList(),
            onDeleteClick = { meal ->
                lifecycleScope.launch {
                    db.mealDao().deleteMeal(meal)
                    Toast.makeText(context, "Recipe deleted", Toast.LENGTH_SHORT).show()
                }
            },
            onScheduleRecipe = { recipe, date, time ->
                scheduleRecipe(recipe, date, time)
            },
            onUpdateRecipe = { updatedRecipe ->
                lifecycleScope.launch {
                    db.mealDao().updateMeal(updatedRecipe)
                    Toast.makeText(context, "Recipe updated", Toast.LENGTH_SHORT).show()
                }
            }
        )

        rvRecipes.layoutManager = LinearLayoutManager(context)
        rvRecipes.adapter = adapter

        // Observe recipes
        lifecycleScope.launch {
            db.mealDao().getAllMeals()
                .map { meals -> meals.filter { it.isRecipe } }
                .collect { recipes ->
                    adapter.updateRecipes(recipes)
                    tvEmptyLibrary.visibility = if (recipes.isEmpty()) View.VISIBLE else View.GONE
                }
        }

        return view
    }

    private fun scheduleRecipe(recipe: Meal, date: String, time: String) {
        lifecycleScope.launch {
            // Create a new meal from the recipe
            val meal = Meal(
                name = recipe.name,
                description = recipe.description,
                date = date,
                time = time,
                timestamp = parseDateTime(date, time),
                isRecipe = false, // This is a scheduled meal, not a recipe
                ingredients = recipe.ingredients,
                cookingTime = recipe.cookingTime,
                servings = recipe.servings,
                instructions = recipe.instructions
            )
            
            db.mealDao().insertMeal(meal)
            Toast.makeText(context, "Recipe scheduled for $date at $time", Toast.LENGTH_SHORT).show()
        }
    }

    private fun parseDateTime(date: String, time: String): Long {
        val calendar = Calendar.getInstance()
        
        // Parse date (dd/MM/yyyy)
        val dateParts = date.split("/")
        val day = dateParts[0].toInt()
        val month = dateParts[1].toInt() - 1 // Calendar months are 0-based
        val year = dateParts[2].toInt()
        
        // Parse time (HH:mm)
        val timeParts = time.split(":")
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()

        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }
}
