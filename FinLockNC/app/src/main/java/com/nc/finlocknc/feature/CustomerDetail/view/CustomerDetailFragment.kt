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
import com.nc.finlocknc.feature.CustomerDetail.adapter.FeatureControlAdapter
import com.nc.finlocknc.feature.CustomerDetail.adapter.RecentCommandsAdapter
import com.nc.finlocknc.feature.CustomerDetail.model.request.CustomerDetail
import com.nc.finlocknc.feature.CustomerDetail.model.request.DeviceInfo
import com.nc.finlocknc.feature.CustomerDetail.model.request.EMIInfo
import com.nc.finlocknc.feature.CustomerDetail.model.request.FeatureControl
import com.nc.finlocknc.feature.CustomerDetail.model.request.LocationInfo
import com.nc.finlocknc.feature.CustomerDetail.model.request.RecentCommand
import com.nc.finlocknc.feature.OngoingLoan.model.request.CustomerLoanData
import com.nc.finlocknc.feature.home.view.HomeActivity

class CustomerDetailFragment : Fragment() {

    private var _binding: FragmentCustomerDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var featureAdapter: FeatureControlAdapter
    private lateinit var recentCommandsAdapter: RecentCommandsAdapter

    private var loanData: CustomerLoanData? = null

    companion object {
        private const val ARG_LOAN_DATA = "loan_data"

        fun newInstance(loan: CustomerLoanData): CustomerDetailFragment {
            val fragment = CustomerDetailFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARG_LOAN_DATA, loan)
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

        loanData = arguments?.getSerializable(ARG_LOAN_DATA) as? CustomerLoanData

        setupToolbar()
        setupRecyclerViews()
        setupClickListeners()

        if (loanData != null) {
            bindCustomerData(loanData!!)
        } else {
            Toast.makeText(requireContext(), "Customer data not found", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun bindCustomerData(loan: CustomerLoanData) {
        val customerDetail = convertToCustomerDetail(loan)
        bindCustomer(customerDetail)
        featureAdapter.updateList(customerDetail.featureControls)
        recentCommandsAdapter.updateList(customerDetail.recentCommands)
    }

    private fun convertToCustomerDetail(loan: CustomerLoanData): CustomerDetail {
        // ✅ Use product_amount instead of loan_amount
        val loanAmount = "₹${loan.product_amount ?: "0"}"
        val emiAmount = "₹${loan.emi_amount ?: "0"}"
        val paidMonths = loan.paid_months ?: 0
        val tenureMonths = loan.tenure_months ?: 0

        // Calculate EMI progress
        val totalLoanValue = loan.product_amount?.replace("₹", "")?.replace(",", "")?.trim()?.toDoubleOrNull() ?: 0.0
        val emiValue = loan.emi_amount?.replace("₹", "")?.replace(",", "")?.trim()?.toDoubleOrNull() ?: 0.0
        val paidAmount = paidMonths * emiValue
        val pendingAmount = totalLoanValue - paidAmount

        // ✅ Determine EMI status based on loan status field
        val loanStatus = loan.status ?: "close"
        val emiStatus = if (loanStatus == "open") "Active" else "Inactive"

        return CustomerDetail(
            customerName = loan.name ?: "N/A",
            customerId = "#${loan.id}",
            mobileNumber = loan.contact ?: "N/A",
            loanAmount = loanAmount,
            emiStatus = emiStatus, // ✅ Now shows Active/Inactive based on status
            deviceInfo = DeviceInfo(
                deviceName = "${loan.mobile_brand ?: ""} ${loan.mobile_model ?: ""}".trim().ifEmpty { "N/A" },
                imei = loan.imei ?: "N/A",
                onlineStatus = true,
                lockStatus = "Unlocked",
                battery = 82,
                lastSeen = "N/A"
            ),
            locationInfo = LocationInfo(
                latitude = 0.0,
                longitude = 0.0
            ),
            emiInfo = EMIInfo(
                totalLoan = loanAmount,
                paidAmount = "₹${String.format("%.0f", paidAmount)}",
                pendingAmount = "₹${String.format("%.0f", pendingAmount)}",
                nextEmiDate = loan.next_emi_date ?: "N/A",
                penalty = "₹0"
            ),
            retailerNotes = "Customer: ${loan.name}",
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

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
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

        // ✅ Bind EMI Status Chip with correct Active/Inactive status
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

        // ✅ Set background and text color based on status
        if (status.equals("Active", ignoreCase = true)) {
            binding.tvEmiStatus.setBackgroundResource(R.drawable.bg_chip_status_active)
            binding.tvEmiStatus.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.color_success)
            )
        } else {
            binding.tvEmiStatus.setBackgroundResource(R.drawable.bg_chip_status_inactive)
            binding.tvEmiStatus.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.color_danger)
            )
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
        if (status.equals("Locked", ignoreCase = true)) {
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