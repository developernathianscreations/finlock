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
            // ✅ Use product_amount instead of loan_amount
            binding.tvLoanAmount.text = "₹${loan.product_amount ?: "0"}"
            binding.tvEmiAmount.text = "₹${loan.emi_amount ?: "0"}"

            // Tenure display
            val tenureMonths = loan.tenure_months ?: 0
            val paidMonths = loan.paid_months ?: 0
            binding.tvTenure.text = "$paidMonths/$tenureMonths mo"

            binding.tvNextEmiDate.text = loan.next_emi_date ?: "N/A"
            binding.tvStatus.text = loan.emi_status ?: "Active"

            // Calculate progress percentage
            val progressPercent = if (tenureMonths > 0) {
                (paidMonths * 100) / tenureMonths
            } else {
                0
            }
            binding.tvProgressPercent.text = "$progressPercent%"
            binding.progressRepayment.progress = progressPercent

            // Set icon
            binding.tvLoanIcon.setImageResource(R.drawable.ic_person)

            // Set status color
            val status = loan.emi_status ?: "Active"
            if (status == "Active") {
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