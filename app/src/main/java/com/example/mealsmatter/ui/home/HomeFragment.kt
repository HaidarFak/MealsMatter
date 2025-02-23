package com.example.mealsmatter.ui.home

import android.widget.Toast
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mealsmatter.R
import com.example.mealsmatter.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Declare views
    private lateinit var tvGreeting: TextView
    private lateinit var rvUpcomingMeals: RecyclerView
    private lateinit var btnPlanMeal: Button
    private lateinit var btnViewGroceryList: Button
    private lateinit var tvDailyTip: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize views
        tvGreeting = root.findViewById(R.id.tv_greeting)
        rvUpcomingMeals = root.findViewById(R.id.rv_upcoming_meals)
        btnPlanMeal = root.findViewById(R.id.btn_plan_meal)
        btnViewGroceryList = root.findViewById(R.id.btn_view_grocery_list)
        tvDailyTip = root.findViewById(R.id.tv_daily_tip)

        // Set up RecyclerView for upcoming meals
        rvUpcomingMeals.layoutManager = LinearLayoutManager(requireContext())
        rvUpcomingMeals.adapter = UpcomingMealsAdapter(getUpcomingMeals())

        // Set up button click listeners
        btnPlanMeal.setOnClickListener {
            // Navigate to the meal planning screen
            // Example: findNavController().navigate(R.id.action_homeFragment_to_mealPlanFragment)
            showToast("Navigate to Plan Meal Screen")
        }

        btnViewGroceryList.setOnClickListener {
            // Navigate to the grocery list screen
            // Example: findNavController().navigate(R.id.action_homeFragment_to_groceryListFragment)
            showToast("Navigate to Grocery List Screen")
        }

        // Set daily tip
        tvDailyTip.text = getDailyTip()

        // Observe ViewModel data (if needed)
        homeViewModel.text.observe(viewLifecycleOwner) {
            // Update UI with ViewModel data
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Helper function to get upcoming meals (dummy data for now)
    private fun getUpcomingMeals(): List<String> {
        return listOf(
            "Breakfast: Oatmeal (Today at 8:00 AM)",
            "Lunch: Grilled Chicken Salad (Today at 1:00 PM)",
            "Dinner: Pasta (Today at 7:00 PM)"
        )
    }

    // Helper function to get a daily tip (dummy data for now)
    private fun getDailyTip(): String {
        return "Did you know? Eating breakfast boosts your metabolism!"
    }

    // Helper function to show a toast message
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}