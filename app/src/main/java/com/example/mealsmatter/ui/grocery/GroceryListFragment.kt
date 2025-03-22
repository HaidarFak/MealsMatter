package com.example.mealsmatter.ui.grocery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealsmatter.data.GroceryItem
import com.example.mealsmatter.databinding.FragmentGroceryListBinding

class GroceryListFragment : Fragment() {

    private var _binding: FragmentGroceryListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: GroceryListAdapter
    private val groceryItems = mutableListOf<GroceryItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroceryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GroceryListAdapter(
            groceryItems,
            onEditClick = { item -> /* TODO: Show edit dialog */ },
            onDeleteClick = { item ->
                groceryItems.remove(item)
                adapter.updateItems(groceryItems)
            },
            onCheckChanged = { item, isChecked ->
                item.isChecked = isChecked
            }
        )

        binding.rvGroceryList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGroceryList.adapter = adapter

        // Dummy data for testing
        groceryItems.add(GroceryItem(name = "Milk", quantity = "2 L"))
        groceryItems.add(GroceryItem(name = "Eggs", quantity = "1 dozen"))
        adapter.updateItems(groceryItems)

        binding.fabAddItem.setOnClickListener {
            // TODO: Add dialog to input new grocery item
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
                    groceryItems.add(newItem)
                    adapter.updateItems(groceryItems)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

