package com.nc.finlocknc.feature.OngoingLoan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nc.finlocknc.R
import com.nc.finlocknc.databinding.ItemOngoingLoanBinding
import com.nc.finlocknc.feature.OngoingLoan.model.request.OngoingLoan
import java.text.SimpleDateFormat
import java.util.*

class OngoingLoanAdapter(
    private var loanList: MutableList<OngoingLoan>,
    private val onItemClick: (OngoingLoan) -> Unit
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

    fun updateList(newList: MutableList<OngoingLoan>) {
        loanList = newList
        notifyDataSetChanged()
    }

    inner class LoanViewHolder(
        private val binding: ItemOngoingLoanBinding,
        private val onItemClick: (OngoingLoan) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(loan: OngoingLoan) {
            binding.tvCustomerName.text = loan.customerName
            binding.tvLoanId.text = loan.loanId
            binding.tvLoanAmount.text = loan.loanAmount
            binding.tvEmiAmount.text = loan.emiAmount
            binding.tvTenure.text = loan.tenure
            binding.tvNextEmiDate.text = loan.nextEmiDate
            binding.tvStatus.text = loan.status

            // Calculate progress percentage
            val progressPercent = if (loan.tenureMonths > 0) {
                (loan.paidMonths * 100) / loan.tenureMonths
            } else {
                0
            }
            binding.tvProgressPercent.text = "$progressPercent%"
            binding.progressRepayment.progress = progressPercent

            // Set random icon
            binding.tvLoanIcon.setImageResource(R.drawable.ic_person)

            // Set status color
            if (loan.status == "Active") {
                binding.tvStatus.setBackgroundResource(R.drawable.bg_signature_status_success)
                binding.tvStatus.setTextColor(binding.root.context.getColor(R.color.green))
            } else {
                binding.tvStatus.setBackgroundResource(R.drawable.bg_sig_status_error)
                binding.tvStatus.setTextColor(binding.root.context.getColor(R.color.red))
            }

            // Apply date-based color coding
            applyDateBasedColor(loan.nextEmiDate)

            // Set click listener on the entire card
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