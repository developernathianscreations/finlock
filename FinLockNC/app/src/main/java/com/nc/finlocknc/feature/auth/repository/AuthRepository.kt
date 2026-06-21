package com.nc.finlocknc.feature.auth.repository

interface AuthRepository {

    fun saveMPin(mpin: String)

    fun getMPin(): String

    fun validateMPin(mpin: String): Boolean
}