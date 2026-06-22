package com.nc.finlocknc.feature.OngoingLoan.model.response

import com.nc.finlocknc.feature.OngoingLoan.model.request.CustomerLoanData

data class LoanCustomerResponse(
    val status: String,
    val data: List<CustomerLoanData>
)
