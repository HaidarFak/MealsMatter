package com.example.mealsmatter.ui.mealselect

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.CheckBox
import android.widget.TextView

class MealExpandableListAdapter(
    private val context: Context,
    private val mealData: Map<String, List<String>>,
    private val selectedItems: MutableMap<String, MutableList<String>>
) : BaseExpandableListAdapter() {

    private val mealCategories = mealData.keys.toList()

    override fun getGroupCount() = mealCategories.size
    override fun getChildrenCount(groupPosition: Int) = mealData[mealCategories[groupPosition]]!!.size
    override fun getGroup(groupPosition: Int) = mealCategories[groupPosition]
    override fun getChild(groupPosition: Int, childPosition: Int) =
        mealData[mealCategories[groupPosition]]!![childPosition]
    override fun getGroupId(groupPosition: Int) = groupPosition.toLong()
    override fun getChildId(groupPosition: Int, childPosition: Int) = childPosition.toLong()
    override fun hasStableIds() = false
    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val textView = TextView(context)
        textView.setPadding(64, 32, 0, 32)
        textView.text = getGroup(groupPosition)
        textView.textSize = 18f
        return textView
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean,
                              convertView: View?, parent: ViewGroup?): View {
        val checkbox = CheckBox(context)
        val group = getGroup(groupPosition)
        val item = getChild(groupPosition, childPosition)
        checkbox.text = item
        checkbox.isChecked = selectedItems[group]?.contains(item) == true

        checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedItems[group]?.add(item)
            } else {
                selectedItems[group]?.remove(item)
            }
        }
        return checkbox
    }
}
