package com.nc.finlocknc.feature.TopUpKeys.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nc.finlocknc.R
import com.nc.finlocknc.databinding.FragmentTopUpKeysBinding
import com.nc.finlocknc.feature.TopUpKeys.adatper.KeyAdapter
import com.nc.finlocknc.feature.TopUpKeys.model.request.KeyTransaction
import com.nc.finlocknc.feature.home.view.HomeActivity

class TopUpKeysFragment : Fragment() {

    private var _binding: FragmentTopUpKeysBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: KeyAdapter

    private val keyTransactions = mutableListOf(
        KeyTransaction("Alice Smith", "KEY-PLK-87654", "Acct: #PLK-87654", "Active", "2 days ago"),
        KeyTransaction("Bob Johnson", "KEY-PLK-76543", "Acct: #PLK-76543", "Active", "5 days ago"),
        KeyTransaction("Carol Davis", "KEY-PLK-65432", "Acct: #PLK-65432", "Inactive", "1 week ago"),
        KeyTransaction("David Wilson", "KEY-PLK-54321", "Acct: #PLK-54321", "Active", "2 weeks ago"),
        KeyTransaction("Emma Brown", "KEY-PLK-43210", "Acct: #PLK-43210", "Active", "3 weeks ago"),
        KeyTransaction("Frank Miller", "KEY-PLK-32109", "Acct: #PLK-32109", "Inactive", "1 month ago")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopUpKeysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        setupStats()
        setupClickListeners()
    }
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun setupRecyclerView() {
        adapter = KeyAdapter(keyTransactions)
        binding.recyclerKeys.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerKeys.adapter = adapter
        binding.recyclerKeys.setHasFixedSize(true)
    }

    private fun setupStats() {
        val totalKeys = 25000
        val usedKeys = 21500
        val remainingKeys = 3500
        val usedPercent = (usedKeys * 100 / totalKeys)

        binding.tvTotalKeys.text = String.format("%,d", totalKeys)
        binding.tvUsedKeys.text = String.format("%,d", usedKeys)
        binding.tvRemainingKeys.text = String.format("%,d", remainingKeys)
        binding.tvUsedPercent.text = "$usedPercent%"
        binding.progressUsage.progress = usedPercent
    }

    private fun setupClickListeners() {
        // FAB Button
        binding.fabTopUp.setOnClickListener {
            Toast.makeText(requireContext(), "Generate new keys feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        // Toolbar navigation
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // View All button
/*
        binding.tvViewAll.setOnClickListener {
            Toast.makeText(requireContext(), "View all transactions", Toast.LENGTH_SHORT).show()
        }
*/
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