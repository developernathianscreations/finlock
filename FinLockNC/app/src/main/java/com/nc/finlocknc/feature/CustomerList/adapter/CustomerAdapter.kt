package com.nc.finlocknc.feature.CustomerList.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nc.finlocknc.databinding.ItemCustomerBinding
import com.nc.finlocknc.feature.CustomerList.model.request.CustomerList

class CustomerAdapter(
    private var customerList: MutableList<CustomerList>,
    private val onCustomerClick: (CustomerList) -> Unit
) : RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>() {

    inner class CustomerViewHolder(
        private val binding: ItemCustomerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(customer: CustomerList) {

            binding.tvCustomerName.text =
                customer.customerName

            binding.tvMobile.text =
                customer.mobileNumber

            binding.tvLoanAmount.text =
                customer.loanAmount

            binding.tvStatus.text =
                customer.status

            binding.tvLoanId.text =
                customer.loanId

            binding.root.setOnClickListener {

                onCustomerClick(customer)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomerViewHolder {

        val binding =
            ItemCustomerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CustomerViewHolder,
        position: Int
    ) {

        holder.bind(customerList[position])
    }

    override fun getItemCount(): Int {

        return customerList.size
    }

    fun updateList(
        list: MutableList<CustomerList>
    ) {

        customerList = list

        notifyDataSetChanged()
    }
}