package com.example.mealsmatter.ui.home

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mealsmatter.R
import com.example.mealsmatter.data.Meal
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import com.google.android.material.button.MaterialButton
import java.util.Calendar

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
    private val onDeleteClick: (Meal) -> Unit,
    private val onEditClick: (Meal, String, String, String, String) -> Unit
) : RecyclerView.Adapter<UpcomingMealsAdapter.ViewHolder>() {

    private var editingPosition = -1
    private var expandedPosition = -1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mealName: TextView = view.findViewById(R.id.tv_meal_name)
        val mealTime: TextView = view.findViewById(R.id.tv_meal_time)
        val mealDescription: TextView = view.findViewById(R.id.tv_meal_description)
        val mealDescriptionExpanded: TextView = view.findViewById(R.id.tv_meal_description_expanded)
        val editButton: ImageButton = view.findViewById(R.id.btn_edit_meal)
        val deleteButton: ImageButton = view.findViewById(R.id.btn_delete_meal)

        // EditTexts for editing mode
        val editMealName: EditText = EditText(view.context).apply {
            visibility = View.GONE
        }
        val editMealDescription: EditText = EditText(view.context).apply {
            visibility = View.GONE
        }
        
        // Buttons for date and time
        val editDateButton = MaterialButton(view.context).apply {
            text = "Change Date"
            visibility = View.GONE
        }
        val editTimeButton = MaterialButton(view.context).apply {
            text = "Change Time"
            visibility = View.GONE
        }

        // Variables to store new date and time
        var newDate: String = ""
        var newTime: String = ""

        init {
            // Add EditTexts and Buttons to the view hierarchy
            val parent = mealName.parent as ViewGroup
            parent.addView(editMealName)
            parent.addView(editMealDescription)
            parent.addView(editDateButton)
            parent.addView(editTimeButton)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_upcoming_meal, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val meal = meals[position]
        
        // Set up normal view mode
        holder.mealName.text = meal.name
        holder.mealTime.text = "${meal.date} at ${meal.time}"
        holder.mealDescription.text = meal.description
        holder.mealDescriptionExpanded.text = meal.description
        
        // Set up edit mode views
        holder.editMealName.setText(meal.name)
        holder.editMealDescription.setText(meal.description)
        holder.newDate = meal.date
        holder.newTime = meal.time

        // Handle visibility based on edit mode
        val isEditing = position == editingPosition
        val isExpanded = position == expandedPosition

        holder.mealName.visibility = if (isEditing) View.GONE else View.VISIBLE
        holder.mealDescription.visibility = if (isEditing || isExpanded) View.GONE else View.VISIBLE
        holder.mealDescriptionExpanded.visibility = if (!isEditing && isExpanded) View.VISIBLE else View.GONE
        holder.mealTime.visibility = if (isEditing) View.GONE else View.VISIBLE
        holder.editMealName.visibility = if (isEditing) View.VISIBLE else View.GONE
        holder.editMealDescription.visibility = if (isEditing) View.VISIBLE else View.GONE
        holder.editDateButton.visibility = if (isEditing) View.VISIBLE else View.GONE
        holder.editTimeButton.visibility = if (isEditing) View.VISIBLE else View.GONE

        // Set up date picker
        holder.editDateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                holder.itemView.context,
                { _, year, month, day ->
                    holder.newDate = "$day/${month + 1}/$year"
                    holder.editDateButton.text = "Date: ${holder.newDate}"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Set up time picker
        holder.editTimeButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                holder.itemView.context,
                { _, hour, minute ->
                    holder.newTime = String.format("%02d:%02d", hour, minute)
                    holder.editTimeButton.text = "Time: ${holder.newTime}"
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        // Set click listener for the entire item
        holder.itemView.setOnClickListener {
            if (!isEditing) {
                expandedPosition = if (isExpanded) -1 else position
                notifyItemChanged(position)
            }
        }

        // Set up click listeners
        holder.deleteButton.setOnClickListener { onDeleteClick(meal) }
        
        holder.editButton.setOnClickListener {
            if (isEditing) {
                // Save changes
                val newName = holder.editMealName.text.toString()
                val newDescription = holder.editMealDescription.text.toString()
                onEditClick(meal, newName, newDescription, holder.newDate, holder.newTime)
                editingPosition = -1
                notifyItemChanged(position)
            } else {
                // Enter edit mode
                if (editingPosition != -1) {
                    notifyItemChanged(editingPosition)
                }
                editingPosition = position
                holder.editDateButton.text = "Date: ${meal.date}"
                holder.editTimeButton.text = "Time: ${meal.time}"
                notifyItemChanged(position)
            }
        }

        // Update edit button icon based on state
        holder.editButton.setImageResource(
            if (isEditing) R.drawable.ic_save else R.drawable.ic_edit
        )
    }

    override fun getItemCount() = meals.size

    fun updateMeals(newMeals: List<Meal>) {
        meals = newMeals
        notifyDataSetChanged()
    }
}