package com.nc.finlocknc.feature.TopUpKeys.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nc.finlocknc.databinding.FragmentTopUpKeysBinding
import com.nc.finlocknc.feature.OngoingLoan.model.request.CustomerLoanData
import com.nc.finlocknc.feature.OngoingLoan.viewmodel.CustomerLoanListViewModel
import com.nc.finlocknc.feature.TopUpKeys.adatper.KeyAdapter
import com.nc.finlocknc.feature.auth.PrefManager.PrefManager
import com.nc.finlocknc.feature.home.view.HomeActivity

class TopUpKeysFragment : Fragment() {

    private var _binding: FragmentTopUpKeysBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: KeyAdapter
    private lateinit var prefManager: PrefManager

    private val customerViewModel: CustomerLoanListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentTopUpKeysBinding.inflate(
                inflater,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        prefManager =
            PrefManager(requireContext())

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        binding.progressBar.visibility =
            View.GONE

        loadKeyStats()
        fetchCustomerData()
    }

    private fun setupToolbar() {

        binding.toolbar.setNavigationOnClickListener {

            requireActivity()
                .onBackPressedDispatcher
                .onBackPressed()
        }
    }

    private fun setupRecyclerView() {

        adapter =
            KeyAdapter(
                mutableListOf()
            )

        binding.recyclerKeys.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        binding.recyclerKeys.adapter =
            adapter
    }

    private fun fetchCustomerData() {

        showLoading(true)

        val retailerId =
            prefManager.getRetailerId()

        if (retailerId != 0) {

            customerViewModel.fetchCustomerList(
                retailerId.toString()
            )

        } else {

            showLoading(false)

            Toast.makeText(
                requireContext(),
                "Retailer Id not found",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupObservers() {

        customerViewModel.customerLoadData.observe(
            viewLifecycleOwner
        ) { response ->

            showLoading(false)

            if (response != null && response.status == "success") {
                val loanList = response.data
                if (loanList != null && loanList.isNotEmpty()) {
                    // ✅ Update adapter with the data (adapter will reverse it)
                    adapter.updateList(loanList.toMutableList())

                    // ✅ Update stats based on actual data
                    updateLoanStats(loanList)
                } else {
                    adapter.updateList(mutableListOf())
                    Toast.makeText(
                        requireContext(),
                        "No key data available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Failed to load key data",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // use PrefManager values only
            loadKeyStats()
        }

        customerViewModel.loading.observe(
            viewLifecycleOwner
        ) {
            showLoading(it)
        }

        customerViewModel.error.observe(
            viewLifecycleOwner
        ) { error ->

            showLoading(false)

            if (error.isNotEmpty()) {

                Toast.makeText(
                    requireContext(),
                    error,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateLoanStats(
        loans: List<CustomerLoanData>
    ) {
        val total =
            loans.size

        // ✅ Use status field instead of mobile_status
        val active =
            loans.count {
                it.status == "open"
            }

        val inactive =
            total - active

        binding.tvTotalKeys.text =
            total.toString()

        binding.tvUsedKeys.text =
            active.toString()

        binding.tvRemainingKeys.text =
            inactive.toString()

        val percent =
            if (total > 0)
                (active * 100 / total)
            else
                0

        binding.tvUsedPercent.text =
            "$percent%"

        binding.progressUsage.progress =
            percent
    }

    private fun loadKeyStats() {

        binding.tvTotalKeys.text =
            prefManager.getTotalKeys()

        binding.tvUsedKeys.text =
            prefManager.getUsedKeys()

        binding.tvRemainingKeys.text =
            prefManager.getAssignedKeys()

        val total =
            prefManager.getTotalKeys()
                .toIntOrNull() ?: 0

        val used =
            prefManager.getUsedKeys()
                .toIntOrNull() ?: 0

        val percent =
            if (total > 0)
                (used * 100 / total)
            else
                0

        binding.tvUsedPercent.text =
            "$percent%"

        binding.progressUsage.progress =
            percent
    }

    private fun setupClickListeners() {

        binding.fabTopUp.setOnClickListener {

            Toast.makeText(
                requireContext(),
                "Generate new keys feature coming soon!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showLoading(
        show: Boolean
    ) {

        binding.progressBar.visibility =
            if (show)
                View.VISIBLE
            else
                View.GONE

        binding.recyclerKeys.visibility =
            if (show)
                View.GONE
            else
                View.VISIBLE
    }

    override fun onResume() {
        super.onResume()

        (activity as? HomeActivity)
            ?.hideMainUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        (activity as? HomeActivity)
            ?.showMainUi()

        _binding = null
    }
}