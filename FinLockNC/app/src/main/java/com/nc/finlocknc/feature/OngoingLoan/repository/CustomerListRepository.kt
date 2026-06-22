package com.nc.finlocknc.feature.OngoingLoan.repository

import com.nc.finlocknc.core.api.RetrofitClient
import com.nc.finlocknc.feature.OngoingLoan.model.response.LoanCustomerResponse

class CustomerListRepository {
    suspend fun getCustomerList(id: String): LoanCustomerResponse {
        return RetrofitClient.apiService.getCustomerList(id)
    }
}