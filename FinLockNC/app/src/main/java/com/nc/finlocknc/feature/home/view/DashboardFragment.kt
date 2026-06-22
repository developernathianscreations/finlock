package com.nc.finlocknc.feature.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.nc.finlocknc.R
import com.nc.finlocknc.databinding.FragmentDashboardBinding
import com.nc.finlocknc.feature.CreateLoan.view.CreateLoanFragment
import com.nc.finlocknc.feature.OngoingLoan.view.OngoingLoanFragment
import com.nc.finlocknc.feature.TopUpKeys.view.TopUpKeysFragment
import com.nc.finlocknc.feature.auth.PrefManager.PrefManager
import com.nc.finlocknc.feature.home.component.AutoSlideBanner
import com.nc.finlocknc.feature.home.model.request.BannerImageModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var autoSlideBanner: AutoSlideBanner

    // Banner items with text data
    private val bannerImages = listOf(
        BannerImageModel(R.drawable.banner),
        BannerImageModel(R.drawable.banner),
        BannerImageModel(R.drawable.banner)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setupAutoSlideBanner()
        setupClickListeners()
        updateStatistics()
        loadUserData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        autoSlideBanner.stopAutoSlide()
        _binding = null
    }

    private fun initViews() {
        autoSlideBanner = binding.autoSlideBanner
    }

    private fun setupAutoSlideBanner() {
        autoSlideBanner.setBannerItems(
            bannerImages
        ) { position ->
            when (position) {
                0 -> showToast("Banner 1")
                1 -> showToast("Banner 2")
                2 -> showToast("Banner 3")
            }
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .addToBackStack(fragment::class.java.simpleName)
            .commit()
    }

    private fun setupClickListeners() {
        // Create Loan
        binding.cardCreateLoan.setOnClickListener {
            navigateToFragment(CreateLoanFragment())
        }

        // Top Up Keys
        binding.cardTopUp.setOnClickListener {
            navigateToFragment(TopUpKeysFragment())
        }

        // Ongoing Loan
        binding.cardOngoingLoan.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.frameContainer, OngoingLoanFragment())
                .addToBackStack("ongoing_loan")
                .commit()
        }

        // Statistics Cards
        binding.cardTotalLoans.setOnClickListener {
            showToast("📊 Total Loans : ${binding.tvTotalLoans.text}")
        }

        binding.cardOpenLoans.setOnClickListener {
            showToast("🟢 Open Loans : ${binding.tvOpenLoans.text}")
        }

        binding.cardClosedLoans.setOnClickListener {
            showToast("✅ Closed Loans : ${binding.tvClosedLoans.text}")
        }
    }

    private fun updateStatistics() {

        val prefManager =
            PrefManager(requireContext())

        binding.tvTotalLoans.text =
            prefManager.getTotalLoans()

        binding.tvOpenLoans.text =
            prefManager.getOpenLoans()

        binding.tvClosedLoans.text =
            prefManager.getClosedLoans()

        binding.tvTotalKeys.text =
            prefManager.getTotalKeys()

        binding.tvAssignedKeys.text =
            prefManager.getAssignedKeys()

    /*    binding.tvPortfolioAmount.text =
            prefManager.getPortfolioAmount()*/
    }
private fun loadUserData() {

        val prefManager =
            PrefManager(requireContext())

        val userName =
            prefManager.getRetailerName()

        val hour =
            java.util.Calendar.getInstance()
                .get(java.util.Calendar.HOUR_OF_DAY)

        val greeting =
            when {

                hour in 5..11 ->
                    "Good Morning"

                hour in 12..16 ->
                    "Good Afternoon"

                hour in 17..20 ->
                    "Good Evening"

                else ->
                    "Good Night"
            }

        binding.tvGreeting.text =
            greeting

        binding.tvUserName.text =
            if (userName.isNotEmpty())
                "$userName 👋"
            else
                "User 👋"
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}