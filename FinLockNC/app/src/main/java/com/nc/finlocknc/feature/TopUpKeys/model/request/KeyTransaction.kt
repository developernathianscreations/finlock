package com.nc.finlocknc.feature.TopUpKeys.model.request

class KeyTransaction (
    val customerName: String,
    val keyId: String,
    val accountId: String,
    val status: String,
    val date: String
)
