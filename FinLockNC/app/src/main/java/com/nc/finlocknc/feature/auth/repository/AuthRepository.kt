package com.nc.finlocknc.feature.auth.repository

import com.nc.finlocknc.feature.auth.model.response.RetailerDetailResponse
import retrofit2.Response

interface AuthRepository {

    fun saveMPin(mpin: String)

    fun getMPin(): String

    fun validateMPin(mpin: String): Boolean

    suspend fun getCustomerByMobile(
        mobile: String
    ): Response<RetailerDetailResponse>
}