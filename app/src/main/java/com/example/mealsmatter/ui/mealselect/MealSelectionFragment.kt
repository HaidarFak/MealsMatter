package com.example.mealsmatter.ui.mealselect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mealsmatter.R

class MealSelectionFragment : Fragment() {

    private lateinit var expandableListView: ExpandableListView
    private lateinit var selectedItems: MutableMap<String, MutableList<String>>

    private val mealOptions = mapOf(
        "Breakfast" to listOf("Eggs", "Toast", "Cereal", "Milk"),
        "Lunch" to listOf("Rice", "Chicken", "Salad", "Juice"),
        "Dinner" to listOf("Pasta", "Soup", "Fish", "Yogurt")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_meal_selection, container, false)

        val tvDate = view.findViewById<TextView>(R.id.tvDateTitle)
        expandableListView = view.findViewById(R.id.mealExpandableList)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmitMeals)

        val selectedDate = arguments?.getString("selected_date") ?: "No date"
        tvDate.text = selectedDate

        selectedItems = mutableMapOf(
            "Breakfast" to mutableListOf(),
            "Lunch" to mutableListOf(),
            "Dinner" to mutableListOf()
        )

        val adapter = MealExpandableListAdapter(requireContext(), mealOptions, selectedItems)
        expandableListView.setAdapter(adapter)

        btnSubmit.setOnClickListener {
            val bundle = Bundle().apply {
                putString("selected_date", selectedDate)
                putStringArrayList("breakfast", ArrayList(selectedItems["Breakfast"]!!))
                putStringArrayList("lunch", ArrayList(selectedItems["Lunch"]!!))
                putStringArrayList("dinner", ArrayList(selectedItems["Dinner"]!!))
            }
            val summaryFragment = MealSummaryFragment()
            summaryFragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, summaryFragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
