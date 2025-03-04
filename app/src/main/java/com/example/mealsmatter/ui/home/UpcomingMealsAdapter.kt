package com.example.mealsmatter.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mealsmatter.R

// Add this data class at the top level
data class UpcomingMeal(
    val name: String,
    val time: String,
    val calories: Int? = null,
    val servings: Int = 1
)

class UpcomingMealsAdapter(
    private var meals: List<UpcomingMeal>,
    private val onMealClick: (UpcomingMeal) -> Unit
) : RecyclerView.Adapter<UpcomingMealsAdapter.MealViewHolder>() {

    fun updateMeals(newMeals: List<UpcomingMeal>) {
        meals = newMeals
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_upcoming_meal, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = meals[position]
        with(holder) {
            tvMealName.text = meal.name
            tvMealTime.text = meal.time
            
            // Set optional information if available
            meal.calories?.let { calories ->
                tvCalories.text = holder.itemView.context.getString(
                    R.string.calories_format,
                    calories
                )
                tvCalories.visibility = View.VISIBLE
            } ?: run {
                tvCalories.visibility = View.GONE
            }

            // Set click listener
            itemView.setOnClickListener { onMealClick(meal) }
        }
    }

    override fun getItemCount(): Int = meals.size

    class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMealName: TextView = itemView.findViewById(R.id.tv_meal_name)
        val tvMealTime: TextView = itemView.findViewById(R.id.tv_meal_time)
        val tvCalories: TextView = itemView.findViewById(R.id.tv_calories)
    }
}