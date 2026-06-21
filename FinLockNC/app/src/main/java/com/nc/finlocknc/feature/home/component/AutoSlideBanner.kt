package com.nc.finlocknc.feature.home.component

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.nc.finlocknc.R
import com.nc.finlocknc.feature.home.adapter.BannerAdapter
import com.nc.finlocknc.feature.home.model.request.BannerImageModel

class AutoSlideBanner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var viewPager: ViewPager2
    private lateinit var dotsLayout: LinearLayout
    private lateinit var bannerAdapter: BannerAdapter

    private val autoSlideHandler = Handler(Looper.getMainLooper())
    private var autoSlideRunnable: Runnable? = null
    private var isAutoSliding = true
    private var bannerList = mutableListOf<BannerImageModel>()

    companion object {
        private const val AUTO_SLIDE_INTERVAL = 4000L
    }

    init {
        orientation = VERTICAL
        inflate(context, R.layout.component_auto_slide_banner, this)
        initViews()
    }

    private fun initViews() {
        viewPager = findViewById(R.id.bannerViewPager)
        dotsLayout = findViewById(R.id.layout_banner_dots)
    }

    fun setBannerItems(
        items: List<BannerImageModel>,
        onItemClick: (Int) -> Unit
    ) {
        bannerList.clear()
        bannerList.addAll(items)

        bannerAdapter = BannerAdapter(
            bannerList,
            onItemClick
        )
        viewPager.adapter = bannerAdapter

        addDotsIndicator()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDotsIndicator(position)
                resetAutoSlideTimer()
            }
        })

        startAutoSlide()
    }

    private fun addDotsIndicator() {
        dotsLayout.removeAllViews()

        for (i in bannerList.indices) {
            val dot = TextView(context)
            dot.text = "•"
            dot.textSize = 14f
            dot.setTextColor(Color.parseColor("#9CA3AF"))
            dot.setPadding(2, 0, 2, 0)
            dotsLayout.addView(dot)
        }

        updateDotsIndicator(0)
    }

    private fun updateDotsIndicator(position: Int) {
        for (i in 0 until dotsLayout.childCount) {

            val dot = dotsLayout.getChildAt(i) as TextView

            if (i == position) {
                dot.setTextColor(Color.parseColor("#3B82F6"))
                dot.textSize = 18f
            } else {
                dot.setTextColor(Color.parseColor("#9CA3AF"))
                dot.textSize = 14f
            }
        }
    }
    private fun startAutoSlide() {
        autoSlideRunnable = Runnable {
            if (isAutoSliding && bannerAdapter.itemCount > 0) {
                val nextItem = (viewPager.currentItem + 1) % bannerAdapter.itemCount
                viewPager.setCurrentItem(nextItem, true)
                autoSlideHandler.postDelayed(autoSlideRunnable!!, AUTO_SLIDE_INTERVAL)
            }
        }
        autoSlideHandler.postDelayed(autoSlideRunnable!!, AUTO_SLIDE_INTERVAL)
    }

    fun stopAutoSlide() {
        isAutoSliding = false
        autoSlideRunnable?.let { autoSlideHandler.removeCallbacks(it) }
    }

    private fun resetAutoSlideTimer() {
        autoSlideRunnable?.let { autoSlideHandler.removeCallbacks(it) }
        if (isAutoSliding) {
            autoSlideHandler.postDelayed(autoSlideRunnable!!, AUTO_SLIDE_INTERVAL)
        }
    }
}