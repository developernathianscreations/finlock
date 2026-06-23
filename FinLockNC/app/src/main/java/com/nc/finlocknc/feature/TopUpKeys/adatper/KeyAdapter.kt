package com.nc.finlocknc.feature.TopUpKeys.adatper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nc.finlocknc.R
import com.nc.finlocknc.feature.OngoingLoan.model.request.CustomerLoanData

class KeyAdapter(
    private var keyList: MutableList<CustomerLoanData>
) : RecyclerView.Adapter<KeyAdapter.KeyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): KeyViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_key_transaction,
                parent,
                false
            )

        return KeyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: KeyViewHolder,
        position: Int
    ) {
        holder.bind(keyList[position])
    }

    override fun getItemCount(): Int = keyList.size

    fun updateList(
        newList: MutableList<CustomerLoanData>
    ) {
        // ✅ Reverse the list to show latest first
        newList.reverse()
        keyList = newList
        notifyDataSetChanged()
    }

    inner class KeyViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvCustomerName: TextView =
            itemView.findViewById(R.id.tvCustomerName)

        private val tvKeyId: TextView =
            itemView.findViewById(R.id.tvKeyId)

        private val tvAccountId: TextView =
            itemView.findViewById(R.id.tvAccountId)

        private val tvStatus: TextView =
            itemView.findViewById(R.id.tvStatus)

        private val tvDate: TextView =
            itemView.findViewById(R.id.tvDate)

        private val tvKeyIcon: ImageView =
            itemView.findViewById(R.id.tvKeyIcon)

        fun bind(item: CustomerLoanData) {

            tvCustomerName.text =
                item.name ?: "N/A"

            tvKeyId.text =
                item.license_key ?: "N/A"

            tvAccountId.text =
                "Loan ID : ${item.id}"

            // ✅ Format date for better display
            val dateStr = item.created_at ?: ""
            tvDate.text = formatDate(dateStr)

            // ✅ Use status field instead of mobile_status
            val loanStatus = item.status ?: "close"
            val status = if (loanStatus == "open") "Active" else "Inactive"

            tvStatus.text = status

            tvKeyIcon.setImageResource(
                R.drawable.ic_key
            )

            // ✅ Set status color based on loan status
            if (loanStatus == "open") {
                tvStatus.setBackgroundResource(
                    R.drawable.bg_signature_status_success
                )
                tvStatus.setTextColor(
                    itemView.context.getColor(
                        R.color.green
                    )
                )
            } else {
                tvStatus.setBackgroundResource(
                    R.drawable.bg_sig_status_error
                )
                tvStatus.setTextColor(
                    itemView.context.getColor(
                        R.color.red
                    )
                )
            }
        }

        // ✅ Helper function to format date
        private fun formatDate(dateStr: String): String {
            return try {
                val inputFormat = java.text.SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    java.util.Locale.getDefault()
                )
                val outputFormat = java.text.SimpleDateFormat(
                    "dd MMM yyyy, hh:mm a",
                    java.util.Locale.getDefault()
                )
                val date = inputFormat.parse(dateStr)
                if (date != null) {
                    outputFormat.format(date)
                } else {
                    dateStr
                }
            } catch (e: Exception) {
                dateStr
            }
        }
    }
}