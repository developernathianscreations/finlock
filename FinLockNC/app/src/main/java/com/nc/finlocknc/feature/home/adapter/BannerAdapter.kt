package com.nc.finlocknc.feature.home.adapter


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nc.finlocknc.R
import com.nc.finlocknc.feature.home.model.request.BannerImageModel
import com.nc.finlocknc.feature.home.model.request.BannerItem


class BannerAdapter(
    private val items: List<BannerImageModel>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BannerViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.vitem_banner_image, // CHANGE HERE
                parent,
                false
            )

        return BannerViewHolder(view)
    }
    override fun onBindViewHolder(
        holder: BannerViewHolder,
        position: Int
    ) {

        holder.imageBanner.setImageResource(
            items[position].imageRes
        )

        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int = items.size

    class BannerViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val imageBanner: ImageView =
            view.findViewById(R.id.imageBanner)
    }
}