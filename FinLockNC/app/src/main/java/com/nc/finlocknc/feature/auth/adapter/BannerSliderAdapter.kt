package com.nc.finlocknc.feature.auth.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nc.finlocknc.R

class BannerSliderAdapter(
    private val images: List<Int>
) : RecyclerView.Adapter<BannerSliderAdapter.BannerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner_slider, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size

    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.ivBanner)

        fun bind(imageRes: Int) {
            imageView.setImageResource(imageRes)
            // You can set different titles/descriptions based on position
            when (adapterPosition) {
                0 -> {
                 //   titleText.text = "Instant Personal Loan"
                  //  descText.text = "Up to ₹25 Lakhs • 10 mins disbursal"
                }
                1 -> {
                 //   titleText.text = "Zero Fee Transfer"
                  //  descText.text = "Balance transfer at 0% processing fee"
                }
                2 -> {
                  //  titleText.text = "Cashback & Rewards"
                   // descText.text = "Get 5% cashback on bill payments"
                }
            }
        }
    }
}