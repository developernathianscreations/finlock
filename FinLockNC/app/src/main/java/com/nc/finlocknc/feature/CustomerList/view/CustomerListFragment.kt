package com.nc.finlocknc.feature.CustomerList.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.nc.finlocknc.R
import com.nc.finlocknc.databinding.FragmentCustomerListBinding
import com.nc.finlocknc.feature.CustomerDetail.view.CustomerDetailFragment
import com.nc.finlocknc.feature.CustomerList.adapter.CustomerAdapter
import com.nc.finlocknc.feature.CustomerList.model.request.CustomerList
import com.nc.finlocknc.feature.home.view.HomeActivity

class CustomerListFragment : Fragment() {

    // ViewBinding
    private var _binding: FragmentCustomerListBinding? = null
    private val binding get() = _binding!!

    // Adapter
    private lateinit var adapter: CustomerAdapter

    // Data
    private val allCustomers = mutableListOf(
        CustomerList("Rahul Sharma", "9876543210", "₹5,00,000", "Active", "LN001"),
        CustomerList("Amit Patil", "9876543211", "₹2,50,000", "Inactive", "LN002"),
        CustomerList("Priya Singh", "9876543212", "₹7,50,000", "Active", "LN003"),
        CustomerList("Rohit Kumar", "9876543213", "₹3,20,000", "Inactive", "LN004"),
        CustomerList("Sneha Patil", "9876543214", "₹8,00,000", "Active", "LN005"),
        CustomerList("Vikram Mehta", "9876543215", "₹4,50,000", "Active", "LN006"),
        CustomerList("Neha Gupta", "9876543216", "₹1,80,000", "Inactive", "LN007"),
        CustomerList("Rajesh Verma", "9876543217", "₹6,20,000", "Active", "LN008")
    )

    private var currentList = mutableListOf<CustomerList>()
    private var currentFilter = "All"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize ViewBinding
        _binding = FragmentCustomerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupFilters()
        updateSummary()
        filterCustomers()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {

        currentList = allCustomers.toMutableList()

        adapter = CustomerAdapter(currentList) { customer ->

          //  openCustomerDetail(customer)
        }

        binding.recyclerCustomers.layoutManager =
            LinearLayoutManager(requireContext())

        binding.recyclerCustomers.adapter =
            adapter

        binding.recyclerCustomers.setHasFixedSize(true)
    }
/*
    private fun openCustomerDetail(
        customer: CustomerList
    ) {

        parentFragmentManager
            .beginTransaction()
            .replace(
                R.id.frameContainer,
                CustomerDetailFragment.newInstance(
                    customer.loanId
                )
            )
            .addToBackStack("CustomerDetail")
            .commit()
    }
*/
    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCustomers()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupFilters() {
        binding.chipAll.setOnClickListener {
            currentFilter = "All"
            updateChipStyles()
            filterCustomers()
        }

        binding.chipActive.setOnClickListener {
            currentFilter = "Active"
            updateChipStyles()
            filterCustomers()
        }

        binding.chipInactive.setOnClickListener {
            currentFilter = "Inactive"
            updateChipStyles()
            filterCustomers()
        }

        // Set default selection
        updateChipStyles()
    }

    private fun updateChipStyles() {
        val chips = listOf(
            binding.chipAll to "All",
            binding.chipActive to "Active",
            binding.chipInactive to "Inactive"
        )

        chips.forEach { (chip, filter) ->
            if (filter == currentFilter) {
                chip.isChecked = true
                chip.chipBackgroundColor =
                    androidx.core.content.ContextCompat.getColorStateList(requireContext(), R.color.primary)
                chip.setTextColor(android.graphics.Color.WHITE)
            } else {
                chip.isChecked = false
                chip.chipBackgroundColor =
                    androidx.core.content.ContextCompat.getColorStateList(requireContext(), R.color.cardWhite)
                chip.setTextColor(
                    androidx.core.content.ContextCompat.getColor(requireContext(), R.color.textSecondary)
                )
            }
        }
    }

    private fun filterCustomers() {
        val searchText = binding.etSearch.text.toString().trim().lowercase()

        currentList = allCustomers.filter { customer ->
            val matchesFilter = when (currentFilter) {
                "Active" -> customer.status == "Active"
                "Inactive" -> customer.status == "Inactive"
                else -> true
            }

            val matchesSearch = searchText.isEmpty() ||
                    customer.customerName.lowercase().contains(searchText) ||
                    customer.mobileNumber.contains(searchText)

            matchesFilter && matchesSearch
        }.toMutableList()

        adapter.updateList(currentList)

        // Show empty state
        if (currentList.isEmpty()) {
            Toast.makeText(requireContext(), "No customers found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateSummary() {
        binding.tvTotal.text = allCustomers.size.toString()
        binding.tvActive.text = allCustomers.count { it.status == "Active" }.toString()
    }

    override fun onResume() {
        super.onResume()
        (activity as? HomeActivity)?.hideMainUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? HomeActivity)?.showMainUi()
        _binding = null // Avoid memory leaks
    }
}