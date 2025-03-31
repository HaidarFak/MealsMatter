package com.example.mealsmatter.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AdapterView
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
        val languages = arrayOf("English", "Spanish", "French", "German", "Chinese")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            languages
        )
        binding.spinnerLanguage.adapter = adapter

        // Set selected language from preferences
        val currentLanguage = settingsManager.language
        val languageIndex = languages.indexOf(currentLanguage)
        if (languageIndex >= 0) {
            binding.spinnerLanguage.setSelection(languageIndex)
        }

        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLanguage = languages[position]
                if (selectedLanguage != settingsManager.language) {
                    settingsManager.language = selectedLanguage
                    // You would typically restart the activity or reload resources here
                    // to apply the language change, but we'll omit that for simplicity
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nothing to do
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
