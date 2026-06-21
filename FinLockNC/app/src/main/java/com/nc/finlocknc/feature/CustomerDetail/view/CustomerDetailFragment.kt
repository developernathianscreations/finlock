package com.nc.finlocknc.feature.CustomerDetail.view

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nc.finlocknc.R
import com.nc.finlocknc.databinding.FragmentCustomerDetailBinding
import com.nc.finlocknc.feature.CreateLoan.repository.CustomerRepository
import com.nc.finlocknc.feature.CustomerDetail.adapter.FeatureControlAdapter
import com.nc.finlocknc.feature.CustomerDetail.adapter.RecentCommandsAdapter
import com.nc.finlocknc.feature.CustomerDetail.model.request.CustomerDetail
import com.nc.finlocknc.feature.CustomerDetail.model.request.DeviceInfo
import com.nc.finlocknc.feature.CustomerDetail.model.request.EMIInfo
import com.nc.finlocknc.feature.CustomerDetail.model.request.FeatureControl
import com.nc.finlocknc.feature.CustomerDetail.model.request.LocationInfo
import com.nc.finlocknc.feature.CustomerDetail.model.request.RecentCommand
import com.nc.finlocknc.feature.home.view.HomeActivity

class CustomerDetailFragment : Fragment() {

    private var _binding: FragmentCustomerDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var featureAdapter: FeatureControlAdapter
    private lateinit var recentCommandsAdapter: RecentCommandsAdapter

    private var customerId: String = ""
    private val repository = CustomerRepository()

    companion object {
        private const val ARG_CUSTOMER_ID = "customer_id"

        fun newInstance(customerId: String): CustomerDetailFragment {
            val fragment = CustomerDetailFragment()
            val bundle = Bundle()
            bundle.putString(ARG_CUSTOMER_ID, customerId)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerId = arguments?.getString(ARG_CUSTOMER_ID) ?: "LN001"

        setupToolbar()
        setupRecyclerViews()
        setupClickListeners()
        loadCustomerDetails()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity()
                .onBackPressedDispatcher
                .onBackPressed()
        }
    }
    private fun setupRecyclerViews() {
        featureAdapter = FeatureControlAdapter(mutableListOf()) { feature, enabled ->
            handleFeatureToggle(feature.id, enabled)
        }

        binding.recyclerFeatures.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = featureAdapter
        }

        recentCommandsAdapter = RecentCommandsAdapter(mutableListOf())

        binding.recyclerCommands.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recentCommandsAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnLockDevice.setOnClickListener {
            lockDevice()
        }

        binding.btnUnlockDevice.setOnClickListener {
            unlockDevice()
        }

        binding.btnViewMap.setOnClickListener {
            viewLocation()
        }

        binding.btnCallCustomer.setOnClickListener {
            Toast.makeText(requireContext(), "Calling Customer...", Toast.LENGTH_SHORT).show()
        }

        binding.btnSendReminder.setOnClickListener {
            Toast.makeText(requireContext(), "Reminder Sent", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadCustomerDetails() {
        val detail = getCustomerDetail(customerId)
        bindCustomer(detail)
        featureAdapter.updateList(detail.featureControls)
        recentCommandsAdapter.updateList(detail.recentCommands)
    }

    private fun bindCustomer(detail: CustomerDetail) {
        binding.tvCustomerName.text = detail.customerName
        binding.tvCustomerId.text = detail.customerId
        binding.tvMobile.text = detail.mobileNumber
        binding.tvLoanAmount.text = detail.loanAmount
        binding.tvAvatarInitial.text = detail.customerName.trim()
            .firstOrNull()
            ?.uppercaseChar()
            ?.toString()
            ?: "?"

        bindEmiStatusChip(detail.emiStatus)

        binding.tvDeviceName.text = detail.deviceInfo.deviceName
        binding.tvImei.text = detail.deviceInfo.imei
        binding.tvBattery.text = "Battery ${detail.deviceInfo.battery}%"
        bindBatteryProgress(detail.deviceInfo.battery)
        binding.tvLastSeen.text = "Last seen ${detail.deviceInfo.lastSeen}"
        binding.tvLatitude.text = "Lat: ${detail.locationInfo.latitude}"
        binding.tvLongitude.text = "Lng: ${detail.locationInfo.longitude}"
        binding.tvTotalLoan.text = detail.emiInfo.totalLoan
        binding.tvPaidAmount.text = detail.emiInfo.paidAmount
        binding.tvPendingAmount.text = detail.emiInfo.pendingAmount
        binding.tvNextEmiDate.text = "Next EMI: ${detail.emiInfo.nextEmiDate}"
        binding.tvPenalty.text = "Penalty ${detail.emiInfo.penalty}"
        binding.tvRetailerNotes.text = detail.retailerNotes

        bindEmiProgress(paid = detail.emiInfo.paidAmount, total = detail.emiInfo.totalLoan)

        if (detail.deviceInfo.onlineStatus) {
            binding.tvOnlineStatus.text = "Online"
            binding.tvOnlineStatus.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.color_success)
            )
            binding.tvOnlineStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.dot_online, 0, 0, 0
            )
        } else {
            binding.tvOnlineStatus.text = "Offline"
            binding.tvOnlineStatus.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.color_danger)
            )
            binding.tvOnlineStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.dot_offline, 0, 0, 0
            )
        }

        updateLockStatus(detail.deviceInfo.lockStatus)
    }

    private fun bindEmiStatusChip(status: String) {
        binding.tvEmiStatus.text = status.uppercase()
        if (status == "Active") {
            binding.tvEmiStatus.setBackgroundResource(R.drawable.bg_chip_status_active)
        } else {
            binding.tvEmiStatus.setBackgroundResource(R.drawable.bg_chip_status_inactive)
        }
    }

    private fun bindBatteryProgress(level: Int) {
        binding.progressBattery.progress = level
        val tintColor = when {
            level >= 50 -> R.color.color_success
            level >= 20 -> R.color.color_warning
            else -> R.color.color_danger
        }
        binding.progressBattery.progressTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), tintColor))
    }

    private fun bindEmiProgress(paid: String, total: String) {
        val paidValue = paid.toAmountValue()
        val totalValue = total.toAmountValue()
        val percent = if (totalValue > 0) {
            ((paidValue / totalValue) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
        binding.progressEmi.progress = percent
        binding.tvEmiPercent.text = "$percent% Paid"
    }

    private fun String.toAmountValue(): Double {
        return this.replace("₹", "")
            .replace(",", "")
            .trim()
            .toDoubleOrNull() ?: 0.0
    }

    private fun updateLockStatus(status: String) {
        binding.tvLockStatus.text = status.uppercase()
        if (status == "Locked") {
            binding.tvLockStatus.setBackgroundResource(R.drawable.bg_pill_locked)
            binding.btnLockDevice.isEnabled = false
            binding.btnUnlockDevice.isEnabled = true
        } else {
            binding.tvLockStatus.setBackgroundResource(R.drawable.bg_pill_unlocked)
            binding.btnLockDevice.isEnabled = true
            binding.btnUnlockDevice.isEnabled = false
        }
    }

    private fun lockDevice() {
        updateLockStatus("Locked")
        Toast.makeText(requireContext(), "Device Locked Successfully", Toast.LENGTH_SHORT).show()
    }

    private fun unlockDevice() {
        updateLockStatus("Unlocked")
        Toast.makeText(requireContext(), "Device Unlocked Successfully", Toast.LENGTH_SHORT).show()
    }

    private fun handleFeatureToggle(featureId: String, enabled: Boolean) {
        Toast.makeText(
            requireContext(),
            "${if (enabled) "Enabled" else "Disabled"} Feature : $featureId",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun viewLocation() {
        Toast.makeText(requireContext(), "Opening Map...", Toast.LENGTH_SHORT).show()
    }

    fun getCustomerDetail(customerId: String): CustomerDetail {
        return when (customerId) {
            "LN001" -> createCustomer("Rahul Sharma", "LN001", "9876543210", "₹5,00,000", "Active")
            "LN002" -> createCustomer("Amit Patil", "LN002", "9876543211", "₹2,50,000", "Inactive")
            "LN003" -> createCustomer("Priya Singh", "LN003", "9876543212", "₹7,50,000", "Active")
            "LN004" -> createCustomer("Rohit Kumar", "LN004", "9876543213", "₹3,20,000", "Inactive")
            "LN005" -> createCustomer("Sneha Patil", "LN005", "9876543214", "₹8,00,000", "Active")
            "LN006" -> createCustomer("Vikram Mehta", "LN006", "9876543215", "₹4,50,000", "Active")
            "LN007" -> createCustomer("Neha Gupta", "LN007", "9876543216", "₹1,80,000", "Inactive")
            else -> createCustomer("Rajesh Verma", "LN008", "9876543217", "₹6,20,000", "Active")
        }
    }

    private fun createCustomer(
        name: String,
        id: String,
        mobile: String,
        amount: String,
        status: String
    ): CustomerDetail {
        return CustomerDetail(
            customerName = name,
            customerId = id,
            mobileNumber = mobile,
            loanAmount = amount,
            emiStatus = status,
            deviceInfo = DeviceInfo(
                deviceName = "Samsung M35",
                imei = "123456789012345",
                onlineStatus = true,
                lockStatus = "Unlocked",
                battery = 82,
                lastSeen = "2 min ago"
            ),
            locationInfo = LocationInfo(
                latitude = 18.5204,
                longitude = 73.8567
            ),
            emiInfo = EMIInfo(
                totalLoan = amount,
                paidAmount = "₹1,50,000",
                pendingAmount = "₹1,00,000",
                nextEmiDate = "15 July 2026",
                penalty = "₹500"
            ),
            retailerNotes = "Customer detail for $name",
            featureControls = listOf(
                FeatureControl("1", "Schedule Lock", true),
                FeatureControl("2", "WhatsApp Block", true),
                FeatureControl("3", "USB Disable", true),
                FeatureControl("4", "Location Tracking", true)
            ),
            recentCommands = listOf(
                RecentCommand("Lock Device", "Success", "10:30 AM")
            )
        )
    }

    // REMOVED onResume and onDestroyView hide/show calls
    // The HomeActivity now handles hiding/showing
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