package com.nc.finlocknc.feature.OngoingLoan.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nc.finlocknc.R
import com.nc.finlocknc.databinding.FragmentOngoingLoanBinding
import com.nc.finlocknc.feature.CustomerDetail.view.CustomerDetailFragment
import com.nc.finlocknc.feature.OngoingLoan.adapter.OngoingLoanAdapter
import com.nc.finlocknc.feature.OngoingLoan.model.request.CustomerLoanData
import com.nc.finlocknc.feature.OngoingLoan.viewmodel.CustomerLoanListViewModel
import com.nc.finlocknc.feature.auth.PrefManager.PrefManager
import com.nc.finlocknc.feature.home.view.HomeActivity
import java.text.SimpleDateFormat
import java.util.*

class OngoingLoanFragment : Fragment() {

    private var _binding: FragmentOngoingLoanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CustomerLoanListViewModel by viewModels()

    private lateinit var adapter: OngoingLoanAdapter
    private var allLoans = mutableListOf<CustomerLoanData>()
    private var filteredLoans = mutableListOf<CustomerLoanData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOngoingLoanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupStats()
        setupClickListeners()
        setupFilterChips()
        setupObservers()

        fetchLoanData()
    }

    private fun setupObservers() {
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.customerLoadData.observe(viewLifecycleOwner) { response ->
            if (response != null && response.status == "success") {
                val loanList = response.data
                if (loanList != null && loanList.isNotEmpty()) {
                    allLoans.clear()
                    allLoans.addAll(loanList)

                    // ✅ Reverse the list to show latest first
                    allLoans.reverse()

                    applyDefaultSorting()
                    updateStats()
                } else {
                    showNoDataMessage()
                }
            } else {
                showNoDataMessage()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Error: $errorMsg", Toast.LENGTH_SHORT).show()
                showNoDataMessage()
            }
        }
    }

    private fun showNoDataMessage() {
        allLoans.clear()
        filteredLoans.clear()
        adapter.updateList(filteredLoans)
        updateStats()
        Toast.makeText(requireContext(), "No loan data available", Toast.LENGTH_SHORT).show()
    }

    private fun fetchLoanData() {
        val prefManager = PrefManager(requireContext())
        val retailerId = prefManager.getRetailerId().toString()

        if (retailerId.isNotEmpty() && retailerId != "0") {
            viewModel.fetchCustomerList(retailerId)
        } else {
            showNoDataMessage()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        filteredLoans = mutableListOf()

        adapter = OngoingLoanAdapter(filteredLoans) { selectedLoan ->
            navigateToCustomerDetail(selectedLoan)
        }

        binding.recyclerOngoingLoans.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerOngoingLoans.adapter = adapter
        binding.recyclerOngoingLoans.setHasFixedSize(true)
    }

    private fun navigateToCustomerDetail(loan: CustomerLoanData) {
        val fragment = CustomerDetailFragment.newInstance(loan)

        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .addToBackStack("customer_detail")
            .commit()
    }

    private fun sortLoansByPriority(loans: MutableList<CustomerLoanData>): MutableList<CustomerLoanData> {
        // ✅ Sort by ID descending (latest first) while maintaining priority
        return loans.sortedWith(compareByDescending<CustomerLoanData> {
            it.id ?: 0
        }.thenBy { loan ->
            val days = getDaysDifference(loan.next_emi_date ?: "N/A")
            when {
                days < 0 -> 0
                days <= 3 -> 1
                days <= 7 -> 2
                else -> 3
            }
        }).toMutableList()
    }

    private fun setupStats() {
        updateStats()
    }

    private fun updateStats() {
        // ✅ Count active loans based on status == "open"
        val totalActive = allLoans.count { it.status == "open" }
        val totalAmount = allLoans.sumOf {
            try {
                it.product_amount?.replace("₹", "")?.replace(",", "")?.trim()?.toIntOrNull() ?: 0
            } catch (e: Exception) {
                0
            }
        }
        val nextEmiAmount = allLoans.firstOrNull()?.emi_amount ?: "₹0"

        binding.tvTotalActive.text = totalActive.toString()
        binding.tvTotalAmount.text = if (totalAmount > 0) String.format("₹%,d", totalAmount) else "₹0"
        binding.tvNextEmi.text = nextEmiAmount
    }

    private fun setupClickListeners() {
        // Additional click listeners if needed
    }

    private fun setupFilterChips() {
        binding.chipAll.setOnClickListener {
            applyFilter("All")
            updateChipStyles()
        }

        binding.chipExpired.setOnClickListener {
            applyFilter("Expired")
            updateChipStyles()
        }

        binding.chipDueSoon.setOnClickListener {
            applyFilter("DueSoon")
            updateChipStyles()
        }

        binding.chipUpcoming.setOnClickListener {
            applyFilter("Upcoming")
            updateChipStyles()
        }

        binding.chipAll.isChecked = true
        updateChipStyles()
    }

    private fun applyDefaultSorting() {
        if (allLoans.isNotEmpty()) {
            filteredLoans = sortLoansByPriority(allLoans)
        } else {
            filteredLoans.clear()
        }
        adapter.updateList(filteredLoans)
    }

    private fun applyFilter(filter: String) {
        filteredLoans = when (filter) {
            "Expired" -> {
                allLoans.filter { isDateExpired(it.next_emi_date ?: "N/A") }
                    .sortedByDescending { it.id ?: 0 }
                    .sortedBy { getDaysDifference(it.next_emi_date ?: "N/A") }
                    .toMutableList()
            }
            "DueSoon" -> {
                allLoans.filter { isDateDueSoon(it.next_emi_date ?: "N/A") }
                    .sortedByDescending { it.id ?: 0 }
                    .sortedBy { getDaysDifference(it.next_emi_date ?: "N/A") }
                    .toMutableList()
            }
            "Upcoming" -> {
                allLoans.filter { isDateUpcoming(it.next_emi_date ?: "N/A") }
                    .sortedByDescending { it.id ?: 0 }
                    .sortedBy { getDaysDifference(it.next_emi_date ?: "N/A") }
                    .toMutableList()
            }
            else -> {
                if (allLoans.isNotEmpty()) {
                    sortLoansByPriority(allLoans)
                } else {
                    mutableListOf()
                }
            }
        }

        adapter.updateList(filteredLoans)

        if (filteredLoans.isEmpty()) {
            Toast.makeText(requireContext(), "No loans in this category", Toast.LENGTH_SHORT).show()
        } else {
            val count = filteredLoans.size
            val message = when (filter) {
                "Expired" -> "🔴 $count expired loans"
                "DueSoon" -> "🟡 $count loans due soon"
                "Upcoming" -> "✅ $count upcoming loans"
                else -> "$count total loans"
            }
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateChipStyles() {
        val chips = listOf(
            binding.chipAll to "All",
            binding.chipExpired to "Expired",
            binding.chipDueSoon to "DueSoon",
            binding.chipUpcoming to "Upcoming"
        )

        chips.forEach { (chip, filter) ->
            val isSelected = chip.tag == filter

            if (isSelected) {
                chip.isChecked = true
                chip.chipBackgroundColor =
                    androidx.core.content.ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.primary
                    )
                chip.setTextColor(android.graphics.Color.WHITE)
            } else {
                chip.isChecked = false
                chip.chipBackgroundColor =
                    androidx.core.content.ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.cardWhite
                    )
                chip.setTextColor(
                    androidx.core.content.ContextCompat.getColor(
                        requireContext(),
                        R.color.textSecondary
                    )
                )
            }
        }
    }

    private fun isDateExpired(dateStr: String): Boolean = getDaysDifference(dateStr) < 0
    private fun isDateUrgent(dateStr: String): Boolean = getDaysDifference(dateStr) in 0..3
    private fun isDateDueSoon(dateStr: String): Boolean = getDaysDifference(dateStr) in 4..7
    private fun isDateUpcoming(dateStr: String): Boolean = getDaysDifference(dateStr) > 7

    private fun getDaysDifference(dateStr: String): Long {
        return try {
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val dueDate = dateFormat.parse(dateStr)
            val currentDate = Date()
            if (dueDate != null) {
                val diffInMillis = dueDate.time - currentDate.time
                diffInMillis / (24 * 60 * 60 * 1000)
            } else {
                Long.MAX_VALUE
            }
        } catch (e: Exception) {
            Long.MAX_VALUE
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? HomeActivity)?.hideMainUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? HomeActivity)?.showMainUi()
        viewModel.resetState()
        _binding = null
    }
}