package com.example.mealsmatter.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mealsmatter.R
import com.example.mealsmatter.data.Meal

// Add this data class at the top level
data class UpcomingMeal(
    val name: String,
    val time: String,
    val calories: Int? = null,
    val servings: Int = 1
)

class UpcomingMealsAdapter(
    private var meals: List<Meal>,
    private val onMealClick: (Meal) -> Unit,
    private val onDeleteClick: (Meal) -> Unit
) : RecyclerView.Adapter<UpcomingMealsAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mealName: TextView = view.findViewById(R.id.tv_meal_name)
        val mealTime: TextView = view.findViewById(R.id.tv_meal_time)
        val mealDescription: TextView = view.findViewById(R.id.tv_meal_description)
        val deleteButton: ImageButton = view.findViewById(R.id.btn_delete_meal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_upcoming_meal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val meal = meals[position]
        holder.mealName.text = meal.name
        holder.mealTime.text = "${meal.date} at ${meal.time}"
        holder.mealDescription.text = meal.description
        
        holder.itemView.setOnClickListener { onMealClick(meal) }
        holder.deleteButton.setOnClickListener { onDeleteClick(meal) }
    }

    override fun getItemCount() = meals.size

    fun updateMeals(newMeals: List<Meal>) {
        meals = newMeals
        notifyDataSetChanged()
    }
}