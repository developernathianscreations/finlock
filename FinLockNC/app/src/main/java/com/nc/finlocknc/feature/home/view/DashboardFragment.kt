package com.nc.finlocknc.feature.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nc.finlocknc.R
import com.nc.finlocknc.databinding.FragmentDashboardBinding
import com.nc.finlocknc.feature.CreateLoan.view.CreateLoanFragment
import com.nc.finlocknc.feature.OngoingLoan.view.OngoingLoanFragment
import com.nc.finlocknc.feature.TopUpKeys.view.TopUpKeysFragment
import com.nc.finlocknc.feature.auth.LoginViewModelFactory.LoginViewModelFactory
import com.nc.finlocknc.feature.auth.PrefManager.PrefManager
import com.nc.finlocknc.feature.auth.model.response.RetailerDetailResponse
import com.nc.finlocknc.feature.auth.repository.AuthRepositoryImpl
import com.nc.finlocknc.feature.auth.viewmodel.LoginViewModel
import com.nc.finlocknc.feature.home.component.AutoSlideBanner
import com.nc.finlocknc.feature.home.model.request.BannerImageModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var autoSlideBanner: AutoSlideBanner
    private lateinit var prefManager: PrefManager

    // ✅ Add ViewModel for fetching fresh data
    private lateinit var loginViewModel: LoginViewModel

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

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        prefManager = PrefManager(requireContext())

        // ✅ Initialize ViewModel with Factory
        val repository = AuthRepositoryImpl(
            PrefManager(requireContext())
        )

        loginViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(repository)
        )[LoginViewModel::class.java]

        initViews()
        setupAutoSlideBanner()
        setupClickListeners()
        setupObservers()

        loadUserData()
        refreshStatistics()
    }

    override fun onResume() {
        super.onResume()
        // ✅ Refresh data every time user comes back to this fragment
        refreshStatistics()
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

    // ===== SETUP OBSERVERS =====
    private fun setupObservers() {
        // ✅ Observe customer data response to update statistics
        loginViewModel.customerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is com.nc.finlocknc.core.common.UiState.Success -> {
                    val response = state.data
                    if (response.status == "success") {
                        // ✅ Update PrefManager with fresh data
                        updatePrefManagerData(response)
                        // ✅ Refresh UI with updated data
                        updateStatistics()
                    } else {
                        // If API returns error, use cached data
                        updateStatistics()
                    }
                }
                is com.nc.finlocknc.core.common.UiState.Error -> {
                    // ✅ If API fails, still show cached data
                    updateStatistics()
                }
                else -> {
                    // Loading state - don't update
                }
            }
        }
    }

    // ===== UPDATE PREFMANAGER WITH FRESH DATA =====
    private fun updatePrefManagerData(
        response: RetailerDetailResponse
    ) {
        // ✅ Update Retailer Details
        val retailer = response.retailer

        prefManager.saveRetailerName(
            retailer.name ?: ""
        )

        prefManager.saveRetailerEmail(
            retailer.email ?: ""
        )

        prefManager.saveMobile(
            retailer.mobile ?: ""
        )

        prefManager.saveRetailerId(
            retailer.id
        )

        // ✅ Update Keys Details
        val keys = response.keys

        prefManager.saveTotalKeys(
            keys.total_keys.toString()
        )

        prefManager.saveUsedKeys(
            keys.used_keys ?: "0"
        )

        prefManager.saveAssignedKeys(
            keys.assigned_keys ?: "0"
        )

        // ✅ Update Loans Details
        val loans = response.loans

        prefManager.saveTotalLoans(
            loans.total_loans.toString()
        )

        prefManager.saveOpenLoans(
            loans.open_loans ?: "0"
        )

        prefManager.saveClosedLoans(
            loans.closed_loans ?: "0"
        )
    }

    // ===== REFRESH STATISTICS =====
    private fun refreshStatistics() {
        val mobile = prefManager.getMobile()

        if (mobile.isNotEmpty()) {
            // ✅ Fetch fresh data from API
            loginViewModel.fetchCustomerByMobile(mobile)
        } else {
            // ✅ If no mobile, use cached data
            updateStatistics()
        }
    }

    private fun updateStatistics() {
        // ✅ Get data from PrefManager (which now has fresh data)
        binding.tvTotalLoans.text = prefManager.getTotalLoans()
        binding.tvOpenLoans.text = prefManager.getOpenLoans()
        binding.tvClosedLoans.text = prefManager.getClosedLoans()
        binding.tvTotalKeys.text = prefManager.getTotalKeys()
        binding.tvAssignedKeys.text = prefManager.getAssignedKeys()

        // ✅ Update progress or additional stats if needed
        updateProgressStats()
    }

    private fun updateProgressStats() {
        val totalLoans = prefManager.getTotalLoans().toIntOrNull() ?: 0
        val openLoans = prefManager.getOpenLoans().toIntOrNull() ?: 0

        // Calculate percentage for visual indicators
        val openPercent = if (totalLoans > 0) {
            (openLoans * 100) / totalLoans
        } else {
            0
        }

        // You can use this for progress bars or other visual elements
        // binding.progressOpenLoans.progress = openPercent
    }

    private fun loadUserData() {
        val userName = prefManager.getRetailerName()
        val hour = java.util.Calendar.getInstance()
            .get(java.util.Calendar.HOUR_OF_DAY)

        val greeting = when {
            hour in 5..11 -> "Good Morning"
            hour in 12..16 -> "Good Afternoon"
            hour in 17..20 -> "Good Evening"
            else -> "Good Night"
        }

        binding.tvGreeting.text = greeting
        binding.tvUserName.text = if (userName.isNotEmpty()) "$userName 👋" else "User 👋"
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}