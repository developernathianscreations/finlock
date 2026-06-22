package com.nc.finlocknc.feature.CreateLoan.model.response

class AddCustomerResponse (
    val status: String,
    val message: String,
    val loan_id: Int? = null  // Nullable because it may not exist in error case
)