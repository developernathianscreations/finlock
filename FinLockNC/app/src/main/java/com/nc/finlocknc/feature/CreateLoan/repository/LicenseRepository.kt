package com.nc.finlocknc.feature.CreateLoan.repository

import com.nc.finlocknc.core.api.RetrofitClient
import com.nc.finlocknc.feature.CreateLoan.model.response.LicenseKeyResponse
import retrofit2.Response

class LicenseRepository() {
    suspend fun fetchLicenseKey(id: Int): Response<LicenseKeyResponse> {
        return RetrofitClient.apiService.getLicenseKey(id)
    }
}
