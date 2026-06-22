package com.nc.finlocknc.feature.CreateLoan.model.request

import com.nc.finlocknc.feature.CreateLoan.model.response.UploadResponse

data class UploadResult(
    val type: String,
    val response: UploadResponse
)
