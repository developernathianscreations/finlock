package com.nc.finlocknc.feature.CustomerList.model.request



data class CustomerList(
    val customerName: String,
    val mobileNumber: String,
    val loanAmount: String,
    val status: String,
    val loanId: String
)