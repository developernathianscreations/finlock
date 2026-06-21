package com.nc.finlocknc.feature.auth.repository

import com.nc.finlocknc.feature.auth.PrefManager.PrefManager


class AuthRepositoryImpl(
    private val prefManager: PrefManager
) : AuthRepository {

    override fun saveMPin(mpin: String) {
        prefManager.saveMPin(mpin)
    }

    override fun getMPin(): String {
        return prefManager.getMPin()
    }

    override fun validateMPin(mpin: String): Boolean {
        return prefManager.getMPin() == mpin
    }
}
