package com.example.mealsmatter.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mealsmatter.R
import com.example.mealsmatter.databinding.FragmentSettingsBinding
import com.example.mealsmatter.utils.SettingsManager

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

        setupDarkModeSwitch()
        setupLanguageSpinner()
        setupBackButton()
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupDarkModeSwitch() {
        binding.switchDarkMode.isChecked = settingsManager.isDarkMode
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.isDarkMode = isChecked
            val mode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    private fun setupLanguageSpinner() {
        val languages = arrayOf("English", "Español", "Français")
        val languageCodes = arrayOf("en", "es", "fr")
        
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = adapter
        
        // Set selected language
        val currentLanguage = settingsManager.language
        val index = languageCodes.indexOf(currentLanguage)
        if (index >= 0) {
            binding.spinnerLanguage.setSelection(index)
        }
        
        binding.spinnerLanguage.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLanguage = languageCodes[position]
                if (selectedLanguage != settingsManager.language) {
                    settingsManager.language = selectedLanguage
                    requireActivity().recreate()
                }
            }
            
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
