package com.nc.finlocknc.feature.OngoingLoan.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nc.finlocknc.R
import com.nc.finlocknc.databinding.FragmentOngoingLoanBinding
import com.nc.finlocknc.feature.OngoingLoan.adapter.OngoingLoanAdapter
import com.nc.finlocknc.feature.OngoingLoan.model.request.OngoingLoan
import com.nc.finlocknc.feature.home.view.HomeActivity
import java.text.SimpleDateFormat
import java.util.*

class OngoingLoanFragment : Fragment() {

    private var _binding: FragmentOngoingLoanBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: OngoingLoanAdapter
    private var allLoans = mutableListOf<OngoingLoan>()
    private var filteredLoans = mutableListOf<OngoingLoan>()

    private val ongoingLoans = mutableListOf(
        OngoingLoan(
            customerName = "Rahul Sharma",
            loanId = "#LN001",
            loanAmount = "₹5,00,000",
            emiAmount = "₹12,500",
            tenure = "24/48 mo",
            tenureMonths = 48,
            paidMonths = 24,
            nextEmiDate = "15 Dec 2024",
            status = "Active",
            emiDueDate = "15 Dec 2024"
        ),
        OngoingLoan(
            customerName = "Amit Patil",
            loanId = "#LN002",
            loanAmount = "₹2,50,000",
            emiAmount = "₹6,250",
            tenure = "12/24 mo",
            tenureMonths = 24,
            paidMonths = 12,
            nextEmiDate = "18 Jun 2024",
            status = "Inactive",
            emiDueDate = "18 Jun 2024"
        ),
        OngoingLoan(
            customerName = "Priya Singh",
            loanId = "#LN003",
            loanAmount = "₹7,50,000",
            emiAmount = "₹18,750",
            tenure = "36/60 mo",
            tenureMonths = 60,
            paidMonths = 36,
            nextEmiDate = "20 Dec 2024",
            status = "Active",
            emiDueDate = "20 Dec 2024"
        ),
        OngoingLoan(
            customerName = "Rohit Kumar",
            loanId = "#LN004",
            loanAmount = "₹3,20,000",
            emiAmount = "₹8,000",
            tenure = "16/32 mo",
            tenureMonths = 32,
            paidMonths = 16,
            nextEmiDate = "22 Jun 2024",
            status = "Inactive",
            emiDueDate = "22 Jun 2024"
        ),
        OngoingLoan(
            customerName = "Sneha Patil",
            loanId = "#LN005",
            loanAmount = "₹8,00,000",
            emiAmount = "₹20,000",
            tenure = "24/48 mo",
            tenureMonths = 48,
            paidMonths = 24,
            nextEmiDate = "19 Jun 2024",
            status = "Active",
            emiDueDate = "19 Jun 2024"
        ),
        OngoingLoan(
            customerName = "Vikram Mehta",
            loanId = "#LN006",
            loanAmount = "₹4,50,000",
            emiAmount = "₹11,250",
            tenure = "30/36 mo",
            tenureMonths = 36,
            paidMonths = 30,
            nextEmiDate = "25 Jun 2024",
            status = "Active",
            emiDueDate = "25 Jun 2024"
        ),
        OngoingLoan(
            customerName = "Neha Gupta",
            loanId = "#LN007",
            loanAmount = "₹1,80,000",
            emiAmount = "₹4,500",
            tenure = "12/18 mo",
            tenureMonths = 18,
            paidMonths = 12,
            nextEmiDate = "28 Jun 2024",
            status = "Inactive",
            emiDueDate = "28 Jun 2024"
        ),
        OngoingLoan(
            customerName = "Rajesh Verma",
            loanId = "#LN008",
            loanAmount = "₹6,20,000",
            emiAmount = "₹15,500",
            tenure = "20/40 mo",
            tenureMonths = 40,
            paidMonths = 20,
            nextEmiDate = "30 Jun 2024",
            status = "Active",
            emiDueDate = "30 Jun 2024"
        )
    )

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
        applyDefaultSorting()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        allLoans = ongoingLoans.toMutableList()
        filteredLoans = sortLoansByPriority(allLoans)

        // Initialize adapter with click listener
        adapter = OngoingLoanAdapter(filteredLoans) { selectedLoan ->
            navigateToCustomerDetail(selectedLoan)
        }

        binding.recyclerOngoingLoans.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerOngoingLoans.adapter = adapter
        binding.recyclerOngoingLoans.setHasFixedSize(true)
    }

    private fun navigateToCustomerDetail(loan: OngoingLoan) {
        val customerId = loan.loanId.replace("#", "")

        // Navigate using HomeActivity
        (activity as? HomeActivity)?.navigateToCustomerDetail(customerId)

        /*
        Toast.makeText(
            requireContext(),
            "Opening ${loan.customerName}'s details...",
            Toast.LENGTH_SHORT
        ).show()
*/
    }

    private fun sortLoansByPriority(loans: MutableList<OngoingLoan>): MutableList<OngoingLoan> {
        return loans.sortedWith(compareBy { loan ->
            val days = getDaysDifference(loan.nextEmiDate)
            when {
                days < 0 -> 0 // Red - Priority 1
                days <= 3 -> 1 // Orange - Priority 2
                days <= 7 -> 2 // Yellow - Priority 3
                else -> 3 // Green - Priority 4
            }
        }).toMutableList()
    }

    private fun setupStats() {
        val totalActive = ongoingLoans.count { it.status == "Active" }
        val totalAmount = ongoingLoans.sumOf {
            it.loanAmount.replace("₹", "").replace(",", "").toInt()
        }
        val nextEmiAmount = ongoingLoans.firstOrNull()?.emiAmount ?: "₹0"

        binding.tvTotalActive.text = totalActive.toString()
        binding.tvTotalAmount.text = String.format("₹%,d", totalAmount)
        binding.tvNextEmi.text = nextEmiAmount
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

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

        /*
        binding.chipUrgent.setOnClickListener {
            applyFilter("Urgent")
            updateChipStyles()
        }
*/

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
        filteredLoans = sortLoansByPriority(allLoans)
        adapter.updateList(filteredLoans)
    }

    private fun applyFilter(filter: String) {
        filteredLoans = when (filter) {
            "Expired" -> {
                allLoans.filter { isDateExpired(it.nextEmiDate) }
                    .sortedBy { getDaysDifference(it.nextEmiDate) }
                    .toMutableList()
            }

            "Urgent" -> {
                allLoans.filter { isDateUrgent(it.nextEmiDate) }
                    .sortedBy { getDaysDifference(it.nextEmiDate) }
                    .toMutableList()
            }

            "DueSoon" -> {
                allLoans.filter { isDateDueSoon(it.nextEmiDate) }
                    .sortedBy { getDaysDifference(it.nextEmiDate) }
                    .toMutableList()
            }

            "Upcoming" -> {
                allLoans.filter { isDateUpcoming(it.nextEmiDate) }
                    .sortedBy { getDaysDifference(it.nextEmiDate) }
                    .toMutableList()
            }

            else -> {
                sortLoansByPriority(allLoans)
            }
        }

        adapter.updateList(filteredLoans)

        if (filteredLoans.isEmpty()) {
            Toast.makeText(requireContext(), "No loans in this category", Toast.LENGTH_SHORT).show()
        } else {
            val count = filteredLoans.size
            val message = when (filter) {
                "Expired" -> "🔴 $count expired loans"
                "Urgent" -> "🟠 $count urgent loans"
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
            //  binding.chipUrgent to "Urgent",
            binding.chipDueSoon to "DueSoon",
            binding.chipUpcoming to "Upcoming"
        )

        chips.forEach { (chip, filter) ->
            val isSelected = when (filter) {
                "All" -> chip.tag == "All"
                "Expired" -> chip.tag == "Expired"
                "Urgent" -> chip.tag == "Urgent"
                "DueSoon" -> chip.tag == "DueSoon"
                "Upcoming" -> chip.tag == "Upcoming"
                else -> false
            }

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

    private fun isDateExpired(dateStr: String): Boolean {
        return getDaysDifference(dateStr) < 0
    }

    private fun isDateUrgent(dateStr: String): Boolean {
        val days = getDaysDifference(dateStr)
        return days in 0..3
    }

    private fun isDateDueSoon(dateStr: String): Boolean {
        val days = getDaysDifference(dateStr)
        return days in 4..7
    }

    private fun isDateUpcoming(dateStr: String): Boolean {
        val days = getDaysDifference(dateStr)
        return days > 7
    }

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

        _binding = null
    }
}