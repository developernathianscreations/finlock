package com.nc.finlocknc.feature.CreateLoan.repository

import com.nc.finlocknc.core.api.RetrofitClient
import com.nc.finlocknc.feature.CreateLoan.model.request.AddCustomerRequest
import com.nc.finlocknc.feature.CreateLoan.model.response.AddCustomerResponse
import com.nc.finlocknc.feature.CustomerDetail.model.request.CustomerDetail
import com.nc.finlocknc.feature.CustomerDetail.model.request.DeviceInfo
import com.nc.finlocknc.feature.CustomerDetail.model.request.EMIInfo
import com.nc.finlocknc.feature.CustomerDetail.model.request.FeatureControl
import com.nc.finlocknc.feature.CustomerDetail.model.request.LocationInfo
import com.nc.finlocknc.feature.CustomerDetail.model.request.RecentCommand


class CustomerRepository {

    suspend fun addCustomer(request: AddCustomerRequest): AddCustomerResponse {
        return RetrofitClient.apiService.addCustomer(request)
    }
}
