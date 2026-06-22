package com.nc.finlocknc.feature.auth.repository

import com.nc.finlocknc.core.api.RetrofitClient
import com.nc.finlocknc.feature.auth.PrefManager.PrefManager
import com.nc.finlocknc.feature.auth.model.response.RetailerDetailResponse
import retrofit2.Response

class AuthRepositoryImpl(
    private val prefManager: PrefManager
) : AuthRepository {

    override fun saveMPin(mpin: String) {
        prefManager.saveMPin(mpin)
    }

    override fun getMPin(): String {
        return prefManager.getMPin()
    }

    override fun validateMPin(mpin: String): Boolean {
        return prefManager.getMPin() == mpin
    }

    override suspend fun getCustomerByMobile(
        mobile: String
    ): Response<RetailerDetailResponse> {

        return RetrofitClient
            .apiService
            .getCustomerByMobile(mobile)
    }
}