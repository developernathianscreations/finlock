package com.nc.finlocknc.feature.profile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nc.finlocknc.R
import com.nc.finlocknc.databinding.FragmentProfileBinding
import com.nc.finlocknc.feature.auth.PrefManager.PrefManager
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

        val pref = PrefManager(requireContext())

        binding.tvUserName.text =
            pref.getRetailerName()

        binding.tvUserEmail.text =
            pref.getRetailerEmail()

        binding.tvUserId.text =
            pref.getRetailerId().toString()

        binding.tvAvatarInitial.text =
            pref.getRetailerName()
                .take(1)
                .uppercase()

        binding.tvDetailName.text =
            pref.getRetailerName()

        binding.tvDetailEmail.text =
            pref.getRetailerEmail()

        binding.tvDetailMobile.text =
            pref.getMobile()

        binding.tvDetailCustomerId.text =
            pref.getRetailerId().toString()

        binding.tvDetailGender.text =
            "-"

        binding.tvDetailDob.text =
            "-"

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