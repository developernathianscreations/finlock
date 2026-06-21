package com.nc.finlocknc.feature.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.nc.finlocknc.R
import com.nc.finlocknc.feature.CreateLoan.view.CreateLoanFragment
import com.nc.finlocknc.feature.OngoingLoan.view.OngoingLoanFragment
import com.nc.finlocknc.feature.TopUpKeys.view.TopUpKeysFragment
import com.nc.finlocknc.feature.home.component.AutoSlideBanner
import com.nc.finlocknc.feature.home.model.request.BannerImageModel
import com.nc.finlocknc.feature.home.model.request.BannerItem

class DashboardFragment : Fragment() {

    private lateinit var autoSlideBanner: AutoSlideBanner
    private lateinit var tvTotalLoans: TextView
    private lateinit var tvOpenLoans: TextView
    private lateinit var tvClosedLoans: TextView


    // Banner items with text data
    private val bannerImages = listOf(

        BannerImageModel(
            R.drawable.banner
        ),

        BannerImageModel(
            R.drawable.banner
        ),

        BannerImageModel(
            R.drawable.banner
        )
    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupAutoSlideBanner()
        setupClickListeners(view)
        updateStatistics()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        autoSlideBanner.stopAutoSlide()
    }

    private fun initViews(view: View) {
        autoSlideBanner = view.findViewById(R.id.autoSlideBanner)
        tvTotalLoans = view.findViewById(R.id.tv_total_loans)
        tvOpenLoans = view.findViewById(R.id.tv_open_loans)
        tvClosedLoans = view.findViewById(R.id.tv_closed_loans)
    }

    private fun setupAutoSlideBanner() {

        autoSlideBanner.setBannerItems(
            bannerImages
        ) { position ->

            when(position){

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

    private fun setupClickListeners(view: View) {
        // Create Loan
        view.findViewById<LinearLayout>(R.id.card_create_loan)
            .setOnClickListener {
                navigateToFragment(CreateLoanFragment())
            }

        // Top Up Keys
        view.findViewById<LinearLayout>(R.id.card_top_up)
            .setOnClickListener {
                navigateToFragment(TopUpKeysFragment())
            }

        // Ongoing Loan
        view.findViewById<LinearLayout>(R.id.card_ongoing_loan)
            .setOnClickListener {
                parentFragmentManager
                    .beginTransaction()
                    .replace(R.id.frameContainer, OngoingLoanFragment())
                    .addToBackStack("ongoing_loan")
                    .commit()
            }

        // Statistics Cards
        view.findViewById<CardView>(R.id.card_total_loans)
            .setOnClickListener {
                showToast("📊 Total Loans : 33")
            }

        view.findViewById<CardView>(R.id.card_open_loans)
            .setOnClickListener {
                showToast("🟢 Open Loans : 10")
            }

        view.findViewById<CardView>(R.id.card_closed_loans)
            .setOnClickListener {
                showToast("✅ Closed Loans : 23")
            }
    }

    private fun updateStatistics() {
        tvTotalLoans.text = "33"
        tvOpenLoans.text = "10"
        tvClosedLoans.text = "23"
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}