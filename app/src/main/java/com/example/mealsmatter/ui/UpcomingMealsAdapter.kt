package com.example.mealsmatter.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mealsmatter.R

class UpcomingMealsAdapter(private val meals: List<String>) :
    RecyclerView.Adapter<UpcomingMealsAdapter.UpcomingMealViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingMealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item_upcoming_meal, parent, false)
        return UpcomingMealViewHolder(view)
    }

    override fun onBindViewHolder(holder: UpcomingMealViewHolder, position: Int) {
        holder.bind(meals[position])
    }

    override fun getItemCount(): Int = meals.size

    class UpcomingMealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMeal: TextView = itemView.findViewById(R.id.tv_meal)

        fun bind(meal: String) {
            tvMeal.text = meal
        }
    }
}