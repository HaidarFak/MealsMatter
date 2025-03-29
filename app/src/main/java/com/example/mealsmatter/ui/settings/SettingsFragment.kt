package com.example.mealsmatter.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mealsmatter.R
import com.example.mealsmatter.databinding.FragmentSettingsBinding
import com.example.mealsmatter.utils.SettingsManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.navigation.fragment.findNavController
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var settingsManager: SettingsManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        settingsManager = SettingsManager.getInstance(requireContext())
        
        setupToolbar()
        setupThemeSwitch()
        setupNotificationSwitches()
        setupLanguageButton()
    }

    private fun setupToolbar() {
        val toolbar = binding.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.navigation_home)
        }
    }

    private fun setupThemeSwitch() {
        binding.switchDarkMode.isChecked = settingsManager.isDarkMode
        
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.isDarkMode = isChecked
            requireActivity().recreate()
        }
    }

    private fun setupNotificationSwitches() {
        binding.switchMealReminders.isChecked = settingsManager.isMealRemindersEnabled
        binding.switchGroceryReminders.isChecked = settingsManager.isGroceryRemindersEnabled
        
        binding.switchMealReminders.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.isMealRemindersEnabled = isChecked
            Toast.makeText(context, "Meal reminders ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }
        
        binding.switchGroceryReminders.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.isGroceryRemindersEnabled = isChecked
            Toast.makeText(context, "Grocery reminders ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupLanguageButton() {
        binding.btnLanguage.text = when (settingsManager.language) {
            "en" -> "English"
            "es" -> "Español"
            "fr" -> "Français"
            else -> "English"
        }
        
        binding.btnLanguage.setOnClickListener {
            val languages = arrayOf("English", "Español", "Français")
            val languageCodes = arrayOf("en", "es", "fr")
            
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select Language")
                .setItems(languages) { _, which ->
                    val selectedLanguage = languageCodes[which]
                    settingsManager.language = selectedLanguage
                    binding.btnLanguage.text = languages[which]
                    Toast.makeText(context, "Language changed. Please restart the app.", Toast.LENGTH_LONG).show()
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 