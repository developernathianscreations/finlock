package com.nc.finlocknc.feature.TopUpKeys.adatper


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nc.finlocknc.R
import com.nc.finlocknc.feature.TopUpKeys.model.request.KeyTransaction

class KeyAdapter(
    private var keyList: MutableList<KeyTransaction>
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
        newList: MutableList<KeyTransaction>
    ) {
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

        fun bind(transaction: KeyTransaction) {

            tvCustomerName.text = transaction.customerName
            tvKeyId.text = transaction.keyId
            tvAccountId.text = transaction.accountId
            tvStatus.text = transaction.status
            tvDate.text = transaction.date

            tvKeyIcon.setImageResource(
                R.drawable.ic_key
            )

            if (transaction.status == "Active") {

                tvStatus.setBackgroundResource(
                    R.drawable.bg_signature_status_success
                )

                tvStatus.setTextColor(
                    itemView.context.getColor(R.color.green)
                )

            } else {

                tvStatus.setBackgroundResource(
                    R.drawable.bg_sig_status_error
                )

                tvStatus.setTextColor(
                    itemView.context.getColor(R.color.red)
                )
            }
        }
    }
}