package com.example.mealsmatter.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mealsmatter.R
import com.example.mealsmatter.data.Meal
import com.google.android.material.button.MaterialButton
import java.util.Calendar

class UpcomingMealsAdapter(
    private var meals: List<Meal>,
    private val onMealClick: (Meal) -> Unit,
    private val onDeleteClick: (Meal) -> Unit,
    private val onEditClick: (Meal, String, String, String, String) -> Unit
) : RecyclerView.Adapter<UpcomingMealsAdapter.ViewHolder>() {

    private var editingPosition = -1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mealName: TextView = view.findViewById(R.id.tv_meal_name)
        val mealTime: TextView = view.findViewById(R.id.tv_meal_time)
        val mealDescription: TextView = view.findViewById(R.id.tv_meal_description)
        val editButton: ImageButton = view.findViewById(R.id.btn_edit_meal)
        val deleteButton: ImageButton = view.findViewById(R.id.btn_delete_meal)
        val editNameInput: EditText = view.findViewById(R.id.edit_meal_name)
        val editDescriptionInput: EditText = view.findViewById(R.id.edit_meal_description)
        val editDateButton: MaterialButton = view.findViewById(R.id.btn_edit_date)
        val editTimeButton: MaterialButton = view.findViewById(R.id.btn_edit_time)
        val saveButton: MaterialButton = view.findViewById(R.id.btn_save)
        val cancelButton: MaterialButton = view.findViewById(R.id.btn_cancel)
        val editContainer: View = view.findViewById(R.id.edit_container)
        val viewContainer: View = view.findViewById(R.id.view_container)
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

        // Set up click listeners
        holder.itemView.setOnClickListener { onMealClick(meal) }
        holder.deleteButton.setOnClickListener { onDeleteClick(meal) }

        // Handle edit mode
        val isEditing = position == editingPosition
        holder.editContainer.visibility = if (isEditing) View.VISIBLE else View.GONE
        holder.viewContainer.visibility = if (isEditing) View.GONE else View.VISIBLE

        if (isEditing) {
            setupEditMode(holder, meal)
        }

        holder.editButton.setOnClickListener {
            editingPosition = if (editingPosition == position) -1 else position
            notifyDataSetChanged()
        }
    }

    private fun setupEditMode(holder: ViewHolder, meal: Meal) {
        holder.editNameInput.setText(meal.name)
        holder.editDescriptionInput.setText(meal.description)
        holder.editDateButton.text = meal.date
        holder.editTimeButton.text = meal.time

        holder.editDateButton.setOnClickListener {
            showDatePicker(holder, meal)
        }

        holder.editTimeButton.setOnClickListener {
            showTimePicker(holder, meal)
        }

        holder.saveButton.setOnClickListener {
            onEditClick(
                meal,
                holder.editNameInput.text.toString(),
                holder.editDescriptionInput.text.toString(),
                holder.editDateButton.text.toString(),
                holder.editTimeButton.text.toString()
            )
            editingPosition = -1
            notifyDataSetChanged()
        }

        holder.cancelButton.setOnClickListener {
            editingPosition = -1
            notifyDataSetChanged()
        }
    }

    private fun showDatePicker(holder: ViewHolder, meal: Meal) {
        val context = holder.itemView.context
        val calendar = Calendar.getInstance()
        
        DatePickerDialog(context, { _, year, month, day ->
            val date = String.format("%02d/%02d/%d", day, month + 1, year)
            holder.editDateButton.text = date
        }, 
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePicker(holder: ViewHolder, meal: Meal) {
        val context = holder.itemView.context
        val calendar = Calendar.getInstance()
        
        TimePickerDialog(context, { _, hour, minute ->
            val time = String.format("%02d:%02d", hour, minute)
            holder.editTimeButton.text = time
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true).show()
    }

    override fun getItemCount() = meals.size
    
    fun updateMeals(newMeals: List<Meal>) {
        meals = newMeals
        notifyDataSetChanged()
    }
}