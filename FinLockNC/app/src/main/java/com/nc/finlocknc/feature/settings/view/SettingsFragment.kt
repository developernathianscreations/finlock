package com.nc.finlocknc.feature.settings.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.nc.finlocknc.R
import com.nc.finlocknc.databinding.FragmentSettingsBinding
import com.nc.finlocknc.feature.home.view.HomeActivity

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

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
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Change MPIN
        binding.cardChangeMpin.setOnClickListener {
            Toast.makeText(requireContext(), "Change MPIN clicked", Toast.LENGTH_SHORT).show()
        }

        // Fingerprint Switch
        binding.switchFingerprint.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                requireContext(),
                if (isChecked) "Fingerprint enabled" else "Fingerprint disabled",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Language
        binding.cardLanguage.setOnClickListener {
            Toast.makeText(requireContext(), "Language selection", Toast.LENGTH_SHORT).show()
        }

        // Notifications Switch
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                requireContext(),
                if (isChecked) "Notifications enabled" else "Notifications disabled",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Dark Mode Switch
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(requireContext(), "Dark mode enabled", Toast.LENGTH_SHORT).show()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Toast.makeText(requireContext(), "Light mode enabled", Toast.LENGTH_SHORT).show()
            }
        }

        // About App
        binding.cardAbout.setOnClickListener {
            Toast.makeText(requireContext(), "About App", Toast.LENGTH_SHORT).show()
        }

        // Privacy Policy
        binding.cardPrivacy.setOnClickListener {
            Toast.makeText(requireContext(), "Privacy Policy", Toast.LENGTH_SHORT).show()
        }

        // Help & Support
        binding.cardHelp.setOnClickListener {
            Toast.makeText(requireContext(), "Help & Support", Toast.LENGTH_SHORT).show()
        }

        // Logout
    }

}