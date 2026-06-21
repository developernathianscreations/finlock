package com.nc.finlocknc.feature.CustomerDetail.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.materialswitch.MaterialSwitch
import com.nc.finlocknc.databinding.ItemFeatureControlBinding
import com.nc.finlocknc.feature.CustomerDetail.model.request.FeatureControl



class FeatureControlAdapter(
    private var featureList: MutableList<FeatureControl>,
    private val onFeatureToggle: (FeatureControl, Boolean) -> Unit
) : RecyclerView.Adapter<FeatureControlAdapter.FeatureViewHolder>() {

    inner class FeatureViewHolder(
        private val binding: ItemFeatureControlBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(feature: FeatureControl) {

            binding.tvFeatureName.text = feature.featureName

            binding.switchFeature.setOnCheckedChangeListener(null)

            binding.switchFeature.isChecked = feature.isEnabled

            binding.switchFeature.setOnCheckedChangeListener { _, isChecked ->

                onFeatureToggle(feature, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeatureViewHolder {

        val binding = ItemFeatureControlBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return FeatureViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FeatureViewHolder,
        position: Int
    ) {
        holder.bind(featureList[position])
    }

    override fun getItemCount(): Int {
        return featureList.size
    }

    fun updateList(
        list: List<FeatureControl>
    ) {
        featureList.clear()
        featureList.addAll(list)
        notifyDataSetChanged()
    }

    fun updateFeatures(
        list: List<FeatureControl>
    ) {
        updateList(list)
    }
}