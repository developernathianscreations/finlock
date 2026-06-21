package com.nc.finlocknc.feature.profile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nc.finlocknc.R
import com.nc.finlocknc.databinding.FragmentProfileBinding
import com.nc.finlocknc.feature.home.view.HomeActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupClickListeners()
        setupProfileData()
    }

    private fun setupToolbar() {

        binding.toolbar.setNavigationOnClickListener {

            requireActivity()
                .onBackPressedDispatcher
                .onBackPressed()
        }
    }

    private fun setupClickListeners() {
        // Contact RM
        binding.tvContactRm.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Connecting to Relationship Manager...",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Edit Profile
        binding.menuEditProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Edit Profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupProfileData() {
        // User Info
        binding.tvUserName.text = "Krushna Shaligram"
        binding.tvUserHandle.text = "@krushna_finlock"
        binding.tvUserEmail.text = "krushna@gmail.com"
        binding.tvUserId.text = "FIN001"
        binding.tvAvatarInitial.text = "K"

        // Personal Details
        binding.tvDetailName.text = "Krushna Shaligram"
        binding.tvDetailDob.text = "08/01/2001"
        binding.tvDetailGender.text = "Male"
        binding.tvDetailEmail.text = "krushna@gmail.com"
        binding.tvDetailMobile.text = "+91 91770 97310"
        binding.tvDetailCustomerId.text = "FIN001"
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