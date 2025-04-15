package com.example.mealsmatter.ui.grocery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mealsmatter.R
import com.example.mealsmatter.data.GroceryItem

// RecyclerView Adapter for displaying and managing grocery list items
class GroceryListAdapter(
    private val items: MutableList<GroceryItem>,
    private val onEditClick: (GroceryItem) -> Unit, // Callback when edit button is clicked
    private val onDeleteClick: (GroceryItem) -> Unit, // Callback when delete button is clicked
    private val onCheckChanged: (GroceryItem, Boolean) -> Unit // Callback when checkbox is toggle
) : RecyclerView.Adapter<GroceryListAdapter.GroceryViewHolder>() {

    // Viewholder holds references to UI components for each grocery item view
    inner class GroceryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemCheckbox: CheckBox = itemView.findViewById(R.id.itemCheckbox)
        val itemQuantity: TextView = itemView.findViewById(R.id.tv_item_quantity)
        val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    // Inflates the layout for each item row
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grocery, parent, false)
        return GroceryViewHolder(view)
    }

    // Binds data to each item view and sets up interaction handlers
    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        val item = items[position]
        holder.itemCheckbox.text = item.name
        holder.itemCheckbox.isChecked = item.isChecked
        holder.itemQuantity.text = item.quantity

        // Prevent incorrect checkbox triggers due to view recycling
        holder.itemCheckbox.setOnCheckedChangeListener(null) // Avoid triggering on recycled views
        holder.itemCheckbox.setOnCheckedChangeListener { _, isChecked ->
            onCheckChanged(item, isChecked)
        }

        holder.editButton.setOnClickListener {
            onEditClick(item)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(item)
        }
    }

    // Returns the total number of grocery items
    override fun getItemCount() = items.size

    // Updates the list of items displayed and refreshes the UI
    fun updateItems(newItems: List<GroceryItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}