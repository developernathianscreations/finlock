package com.nc.finlocknc.feature.auth.model.response

import com.nc.finlocknc.feature.OngoingLoan.model.request.CustomerLoanData
import com.nc.finlocknc.feature.auth.model.request.Keys
import com.nc.finlocknc.feature.auth.model.request.Loans
import com.nc.finlocknc.feature.auth.model.request.RetailerDetail

data class RetailerDetailResponse (
    val status: String,
    val retailer: RetailerDetail,
    val keys: Keys,
    val loans: Loans,
    val message:String,
    val customers: List<CustomerLoanData>?
)