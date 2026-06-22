package com.nc.finlocknc.feature.CreateLoan.model.response

data class LicenseKeyResponse(
    val status: String,
    val license_key: String?
) {
    val isSuccess: Boolean
        get() = status.equals("success", ignoreCase = true)
}
