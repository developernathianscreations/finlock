package com.nc.finlocknc.feature.service.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nc.finlocknc.R
import com.nc.finlocknc.databinding.FragmentServiceBinding
import com.nc.finlocknc.feature.home.view.HomeActivity

class ServiceFragment : Fragment() {

    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupClickListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupClickListeners() {
        // Phone Support
        binding.cardPhoneSupport.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:+9118001234567")
            )
            startActivity(intent)
        }

        // Email Support
        binding.cardEmailSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:support@finlock.com")
            intent.putExtra(Intent.EXTRA_SUBJECT, "Support Request")
            startActivity(intent)
        }

        // FAQ
        binding.cardFaq.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "FAQs Coming Soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Support Person 1
        binding.cardSupportKrushna.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:+919876543210")
            )
            startActivity(intent)
        }

        // Support Person 2
        binding.cardSupportSecond.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:+919876543211")
            )
            startActivity(intent)
        }

        // Feedback
        binding.btnSubmitFeedback.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Feedback Submitted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? HomeActivity)?.hideMainUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? HomeActivity)?.showMainUi()
        _binding = null
    }
}