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

class GroceryListAdapter(
    private val items: MutableList<GroceryItem>,
    private val onEditClick: (GroceryItem) -> Unit,
    private val onDeleteClick: (GroceryItem) -> Unit,
    private val onCheckChanged: (GroceryItem, Boolean) -> Unit
) : RecyclerView.Adapter<GroceryListAdapter.GroceryViewHolder>() {

    inner class GroceryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemCheckbox: CheckBox = itemView.findViewById(R.id.itemCheckbox)
        val itemQuantity: TextView = itemView.findViewById(R.id.tv_item_quantity)
        val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grocery, parent, false)
        return GroceryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        val item = items[position]
        holder.itemCheckbox.text = item.name
        holder.itemCheckbox.isChecked = item.isChecked
        holder.itemQuantity.text = item.quantity

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

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<GroceryItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}