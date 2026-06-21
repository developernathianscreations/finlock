package com.nc.finlocknc.feature.home.model.request

data class BannerItem(
    val icon: String,
    val title: String,
    val description: String,
    val buttonText: String,
    val backgroundColor: String
)

data class BannerImageModel(
    val imageRes: Int
)