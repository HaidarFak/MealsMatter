package com.example.mealsmatter.ui.grocery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealsmatter.data.GroceryItem
import com.example.mealsmatter.data.MealDatabase
import com.example.mealsmatter.databinding.FragmentGroceryListBinding
import com.example.mealsmatter.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GroceryListFragment : Fragment() {

    private var _binding: FragmentGroceryListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: GroceryListAdapter
    private lateinit var db: MealDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroceryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = MealDatabase.getDatabase(requireContext())

        adapter = GroceryListAdapter(
            items = mutableListOf(),
            onEditClick = { item -> showEditItemDialog(item) },
            onDeleteClick = { item -> deleteItem(item) },
            onCheckChanged = { item, isChecked -> updateItemChecked(item, isChecked) }
        )

        binding.rvGroceryList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGroceryList.adapter = adapter

        binding.fabAddItem.setOnClickListener {
            showAddItemDialog()
        }

        // Observe grocery items from database
        lifecycleScope.launch {
            db.groceryItemDao().getAllItems().collect { items ->
                adapter.updateItems(items)
            }
        }
    }

    private fun showAddItemDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_grocery_item, null)

        val itemNameInput = dialogView.findViewById<android.widget.EditText>(R.id.editTextItemName)
        val itemQuantityInput = dialogView.findViewById<android.widget.EditText>(R.id.editTextItemQuantity)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Add Grocery Item")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = itemNameInput.text.toString().trim()
                val quantity = itemQuantityInput.text.toString().trim()

                if (name.isNotEmpty()) {
                    val newItem = GroceryItem(name = name, quantity = quantity)
                    lifecycleScope.launch {
                        db.groceryItemDao().insertItem(newItem)
                        Toast.makeText(context, "Item added", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun showEditItemDialog(item: GroceryItem) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_grocery_item, null)

        val itemNameInput = dialogView.findViewById<android.widget.EditText>(R.id.editTextItemName)
        val itemQuantityInput = dialogView.findViewById<android.widget.EditText>(R.id.editTextItemQuantity)

        // Pre-fill the inputs
        itemNameInput.setText(item.name)
        itemQuantityInput.setText(item.quantity)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Edit Grocery Item")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = itemNameInput.text.toString().trim()
                val quantity = itemQuantityInput.text.toString().trim()

                if (name.isNotEmpty()) {
                    val updatedItem = item.copy(name = name, quantity = quantity)
                    lifecycleScope.launch {
                        db.groceryItemDao().updateItem(updatedItem)
                        Toast.makeText(context, "Item updated", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun deleteItem(item: GroceryItem) {
        lifecycleScope.launch {
            db.groceryItemDao().deleteItem(item)
            Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateItemChecked(item: GroceryItem, isChecked: Boolean) {
        lifecycleScope.launch {
            db.groceryItemDao().updateItem(item.copy(isChecked = isChecked))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

