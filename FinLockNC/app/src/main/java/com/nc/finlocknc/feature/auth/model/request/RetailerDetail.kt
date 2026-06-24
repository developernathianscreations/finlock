package com.nc.finlocknc.feature.auth.model.request


data class RetailerDetail(

    val id: Int,

    val name: String,

    val email: String,

    val mobile: String,

    val city: String,

    val address: String,

    val policyname: String?,

    val active_status: Int,

    val state: String
)
data class Keys(
    val total_keys: Int,
    val assigned_keys: String,
    val used_keys: String
)

data class Loans(
    val total_loans: Int?,
    val open_loans: String?,
    val closed_loans: String?
)