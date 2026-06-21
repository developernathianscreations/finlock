package com.nc.finlocknc.feature.CustomerDetail.adapter


import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nc.finlocknc.databinding.ItemCustomerBinding
import com.nc.finlocknc.databinding.ItemRecentCommandBinding
import com.nc.finlocknc.feature.CustomerDetail.model.request.RecentCommand
import com.nc.finlocknc.feature.CustomerList.model.request.CustomerList


class RecentCommandsAdapter(
    private var commandList: MutableList<RecentCommand>
) : RecyclerView.Adapter<RecentCommandsAdapter.CommandViewHolder>() {

    inner class CommandViewHolder(
        private val binding: ItemRecentCommandBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(command: RecentCommand) {

            binding.tvCommandName.text =
                command.commandName

            binding.tvCommandStatus.text =
                command.status

            binding.tvCommandTime.text =
                command.time

            if (command.status.equals("Success", true)) {

                binding.tvCommandStatus.setTextColor(
                    Color.parseColor("#4CAF50")
                )

            } else {

                binding.tvCommandStatus.setTextColor(
                    Color.parseColor("#F44336")
                )
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommandViewHolder {

        val binding = ItemRecentCommandBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CommandViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CommandViewHolder,
        position: Int
    ) {

        holder.bind(commandList[position])
    }

    override fun getItemCount(): Int {
        return commandList.size
    }

    fun updateList(
        list: List<RecentCommand>
    ) {

        commandList.clear()

        commandList.addAll(list)

        notifyDataSetChanged()
    }

    fun updateCommands(
        list: List<RecentCommand>
    ) {
        updateList(list)
    }
}