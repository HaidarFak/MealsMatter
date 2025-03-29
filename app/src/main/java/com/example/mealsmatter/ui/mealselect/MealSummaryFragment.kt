package com.example.mealsmatter.ui.mealselect

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mealsmatter.R

class MealSummaryFragment : Fragment() {
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_meal_summary, container, false)

        val date = arguments?.getString("selected_date")
        val breakfast = arguments?.getStringArrayList("breakfast") ?: arrayListOf()
        val lunch = arguments?.getStringArrayList("lunch") ?: arrayListOf()
        val dinner = arguments?.getStringArrayList("dinner") ?: arrayListOf()

        view.findViewById<TextView>(R.id.tvSelectedDate).text = "Selected Date: $date"
        view.findViewById<TextView>(R.id.tvBreakfastItems).text = breakfast.joinToString("\n• ", prefix = "• ")
        view.findViewById<TextView>(R.id.tvLunchItems).text = lunch.joinToString("\n• ", prefix = "• ")
        view.findViewById<TextView>(R.id.tvDinnerItems).text = dinner.joinToString("\n• ", prefix = "• ")

        return view
    }
}
