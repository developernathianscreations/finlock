package com.nc.finlocknc.feature.OngoingLoan.model.request



data class OngoingLoan(
    val customerName: String,
    val loanId: String,
    val loanAmount: String,
    val emiAmount: String,
    val tenure: String,
    val tenureMonths: Int,
    val paidMonths: Int,
    val nextEmiDate: String,
    val status: String,
    val emiDueDate: String = "" // Add this field for due date
)