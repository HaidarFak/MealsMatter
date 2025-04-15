package com.example.mealsmatter.ui.library

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.mealsmatter.R
import com.example.mealsmatter.data.Meal
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class RecipeAdapter(
    private var recipes: List<Meal>,
    private val onDeleteClick: (Meal) -> Unit, // Callback to delete a recipe
    private val onScheduleRecipe: (Meal, String, String) -> Unit, // Callback to reuse recipe
    private val onUpdateRecipe: (Meal) -> Unit // Callback to save edited recipe
) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    private var expandedPosition = -1 // Tracks which recipe is expanded

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // View mode views
        val recipeName: TextView = view.findViewById(R.id.tv_recipe_name)
        val cookingTime: TextView = view.findViewById(R.id.tv_cooking_time)
        val servings: TextView = view.findViewById(R.id.tv_servings)
        val recipeDescription: TextView = view.findViewById(R.id.tv_recipe_description)
        val ingredients: TextView = view.findViewById(R.id.tv_ingredients)
        val instructions: TextView = view.findViewById(R.id.tv_instructions)
        
        // Edit input fields
        val etRecipeName: TextInputEditText = view.findViewById(R.id.et_recipe_name)
        val etDescription: TextInputEditText = view.findViewById(R.id.et_recipe_description)
        val etCookingTime: TextInputEditText = view.findViewById(R.id.et_cooking_time)
        val etServings: TextInputEditText = view.findViewById(R.id.et_servings)
        val etIngredients: TextInputEditText = view.findViewById(R.id.et_ingredients)
        val etInstructions: TextInputEditText = view.findViewById(R.id.et_instructions)
        
        // Buttons
        val editRecipeButton: MaterialButton = view.findViewById(R.id.btn_edit_recipe)
        val deleteRecipeButton: MaterialButton = view.findViewById(R.id.btn_delete_recipe)
        val saveEditButton: MaterialButton = view.findViewById(R.id.btn_save_edit)
        val cancelEditButton: MaterialButton = view.findViewById(R.id.btn_cancel_edit)
        val reuseRecipeButton: ImageButton = view.findViewById(R.id.btn_reuse_recipe)
        
        // Layouts
        val recipeDetails: View = view.findViewById(R.id.layout_recipe_details)
        val viewModeLayout: View = view.findViewById(R.id.layout_view_mode)
        val editModeLayout: View = view.findViewById(R.id.layout_edit_mode)
        val editButtonsLayout: View = view.findViewById(R.id.layout_edit_buttons)
        val viewButtonsLayout: View = view.findViewById(R.id.layout_view_buttons)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes[position]
        
        // Display recipe details
        holder.recipeName.text = recipe.name
        holder.cookingTime.text = "${recipe.cookingTime} minutes"
        holder.servings.text = "${recipe.servings} servings"
        holder.recipeDescription.text = recipe.description
        holder.ingredients.text = recipe.ingredients
        holder.instructions.text = recipe.instructions

        // Show/hide expanded details
        val isExpanded = position == expandedPosition
        holder.recipeDetails.visibility = if (isExpanded) View.VISIBLE else View.GONE

        // Ensure we start in view mode
        holder.viewModeLayout.visibility = View.VISIBLE
        holder.editModeLayout.visibility = View.GONE
        holder.viewButtonsLayout.visibility = View.VISIBLE
        holder.editButtonsLayout.visibility = View.GONE

        // Set click listener for the entire item
        holder.itemView.setOnClickListener {
            expandedPosition = if (isExpanded) -1 else position
            notifyItemChanged(position)
        }

        // Set up edit button
        holder.editRecipeButton.setOnClickListener {
            // Switch to edit mode
            holder.viewModeLayout.visibility = View.GONE
            holder.editModeLayout.visibility = View.VISIBLE
            holder.viewButtonsLayout.visibility = View.GONE
            holder.editButtonsLayout.visibility = View.VISIBLE
            
            // Pre-fill edit fields
            holder.etRecipeName.setText(recipe.name)
            holder.etDescription.setText(recipe.description)
            holder.etCookingTime.setText(recipe.cookingTime.toString())
            holder.etServings.setText(recipe.servings.toString())
            holder.etIngredients.setText(recipe.ingredients)
            holder.etInstructions.setText(recipe.instructions)
        }

        // Set up save button
        holder.saveEditButton.setOnClickListener {
            val updatedRecipe = recipe.copy(
                name = holder.etRecipeName.text.toString(),
                description = holder.etDescription.text.toString(),
                cookingTime = holder.etCookingTime.text.toString().toIntOrNull() ?: 0,
                servings = holder.etServings.text.toString().toIntOrNull() ?: 0,
                ingredients = holder.etIngredients.text.toString(),
                instructions = holder.etInstructions.text.toString()
            )
            onUpdateRecipe(updatedRecipe)
            
            // Switch back to view mode
            holder.viewModeLayout.visibility = View.VISIBLE
            holder.editModeLayout.visibility = View.GONE
            holder.viewButtonsLayout.visibility = View.VISIBLE
            holder.editButtonsLayout.visibility = View.GONE
        }

        // Set up cancel button
        holder.cancelEditButton.setOnClickListener {
            // Switch back to view mode without saving
            holder.viewModeLayout.visibility = View.VISIBLE
            holder.editModeLayout.visibility = View.GONE
            holder.viewButtonsLayout.visibility = View.VISIBLE
            holder.editButtonsLayout.visibility = View.GONE
        }

        // Set up delete button
        holder.deleteRecipeButton.setOnClickListener {
            onDeleteClick(recipe)
        }

        // Set up reuse button
        holder.reuseRecipeButton.setOnClickListener { view ->
            showScheduleDialog(view, recipe)
        }
    }

    // Dialog for selecting date and time to reuse/schedule for the recipe
    private fun showScheduleDialog(view: View, recipe: Meal) {
        val dialog = Dialog(view.context)
        dialog.setContentView(R.layout.dialog_schedule_recipe)

        val btnPickDate = dialog.findViewById<Button>(R.id.btn_pick_date)
        val tvSelectedDate = dialog.findViewById<TextView>(R.id.tv_selected_date)
        val btnPickTime = dialog.findViewById<Button>(R.id.btn_pick_time)
        val tvSelectedTime = dialog.findViewById<TextView>(R.id.tv_selected_time)
        val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)
        val btnSchedule = dialog.findViewById<Button>(R.id.btn_schedule)

        val calendar = Calendar.getInstance()

        btnPickDate.setOnClickListener {
            DatePickerDialog(
                view.context,
                R.style.CustomDatePickerDialog,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    tvSelectedDate.text = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnPickTime.setOnClickListener {
            TimePickerDialog(
                view.context,
                R.style.CustomTimePickerDialog,
                { _, hourOfDay, minute ->
                    tvSelectedTime.text = String.format("%02d:%02d", hourOfDay, minute)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSchedule.setOnClickListener {
            val date = tvSelectedDate.text.toString()
            val time = tvSelectedTime.text.toString()
            if (date != "Select Date" && time != "Select Time") {
                onScheduleRecipe(recipe, date, time)
                dialog.dismiss()
            } else {
                Toast.makeText(view.context, "Please select date and time", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    override fun getItemCount() = recipes.size

    // Replaces recipe list and refreshes the view
    fun updateRecipes(newRecipes: List<Meal>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }
}
