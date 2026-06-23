package com.nc.finlocknc.feature.OngoingLoan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nc.finlocknc.R
import com.nc.finlocknc.databinding.ItemOngoingLoanBinding
import com.nc.finlocknc.feature.OngoingLoan.model.request.CustomerLoanData
import java.text.SimpleDateFormat
import java.util.*

class OngoingLoanAdapter(
    private var loanList: MutableList<CustomerLoanData>,
    private val onItemClick: (CustomerLoanData) -> Unit
) : RecyclerView.Adapter<OngoingLoanAdapter.LoanViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanViewHolder {
        val binding = ItemOngoingLoanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LoanViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: LoanViewHolder, position: Int) {
        holder.bind(loanList[position])
    }

    override fun getItemCount(): Int = loanList.size

    fun updateList(newList: MutableList<CustomerLoanData>) {
        loanList = newList
        notifyDataSetChanged()
    }

    inner class LoanViewHolder(
        private val binding: ItemOngoingLoanBinding,
        private val onItemClick: (CustomerLoanData) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(loan: CustomerLoanData) {
            binding.tvCustomerName.text = loan.name ?: "N/A"
            binding.tvLoanId.text = "#${loan.id}"
            binding.tvLoanAmount.text = "₹${loan.product_amount ?: "0"}"
            binding.tvEmiAmount.text = "₹${loan.emi_amount ?: "0"}"

            // Tenure display
            val tenureMonths = loan.no_of_emi ?: 0
            val paidMonths = 0 // Since no paid_months in data, default to 0
            binding.tvTenure.text = "$paidMonths/$tenureMonths mo"

            binding.tvNextEmiDate.text = loan.next_emi_date ?: "N/A"

            // ✅ Set status based on "status" field (open = Active, close = Inactive)
            val loanStatus = loan.status ?: "close"
            val displayStatus = if (loanStatus == "open") "Active" else "Inactive"
            binding.tvStatus.text = displayStatus

            // Calculate progress percentage (using tenure_months if available, else no_of_emi)
            val totalMonths = loan.tenure_months ?: loan.no_of_emi ?: 0
            val progressPercent = if (totalMonths > 0) {
                (paidMonths * 100) / totalMonths
            } else {
                0
            }
            binding.tvProgressPercent.text = "$progressPercent%"
            binding.progressRepayment.progress = progressPercent

            // Set icon
            binding.tvLoanIcon.setImageResource(R.drawable.ic_person)

            // ✅ Set status color based on loan status
            if (loanStatus == "open") {
                binding.tvStatus.setBackgroundResource(R.drawable.bg_signature_status_success)
                binding.tvStatus.setTextColor(binding.root.context.getColor(R.color.green))
            } else {
                binding.tvStatus.setBackgroundResource(R.drawable.bg_sig_status_error)
                binding.tvStatus.setTextColor(binding.root.context.getColor(R.color.red))
            }

            // Apply date-based color coding
            applyDateBasedColor(loan.next_emi_date ?: "N/A")

            // Set click listener
            binding.cardOngoingLoan.setOnClickListener {
                onItemClick(loan)
            }
        }

        private fun applyDateBasedColor(emiDate: String) {
            try {
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val dueDate = dateFormat.parse(emiDate)
                val currentDate = Date()

                if (dueDate != null) {
                    val diffInMillis = dueDate.time - currentDate.time
                    val diffInDays = diffInMillis / (24 * 60 * 60 * 1000)

                    val context = binding.root.context

                    when {
                        diffInDays < 0 -> {
                            binding.cardOngoingLoan.setCardBackgroundColor(
                                context.getColor(R.color.cardRed)
                            )
                            binding.tvDueStatus.text = "🔴 EXPIRED"
                            binding.tvDueStatus.setTextColor(context.getColor(R.color.red))
                            binding.tvNextEmiDate.setTextColor(context.getColor(R.color.red))
                        }
                        diffInDays <= 3 -> {
                            binding.cardOngoingLoan.setCardBackgroundColor(
                                context.getColor(R.color.cardOrange)
                            )
                            binding.tvDueStatus.text = "🟠 URGENT"
                            binding.tvDueStatus.setTextColor(context.getColor(R.color.orange))
                            binding.tvNextEmiDate.setTextColor(context.getColor(R.color.orange))
                        }
                        diffInDays <= 7 -> {
                            binding.cardOngoingLoan.setCardBackgroundColor(
                                context.getColor(R.color.cardYellow)
                            )
                            binding.tvDueStatus.text = "🟡 DUE SOON"
                            binding.tvDueStatus.setTextColor(context.getColor(R.color.yellow_dark))
                            binding.tvNextEmiDate.setTextColor(context.getColor(R.color.yellow_dark))
                        }
                        else -> {
                            binding.cardOngoingLoan.setCardBackgroundColor(
                                context.getColor(R.color.cardWhite)
                            )
                            binding.tvDueStatus.text = "✅ UPCOMING"
                            binding.tvDueStatus.setTextColor(context.getColor(R.color.green))
                            binding.tvNextEmiDate.setTextColor(context.getColor(R.color.primary))
                        }
                    }
                }
            } catch (e: Exception) {
                binding.cardOngoingLoan.setCardBackgroundColor(
                    binding.root.context.getColor(R.color.cardWhite)
                )
            }
        }
    }
}