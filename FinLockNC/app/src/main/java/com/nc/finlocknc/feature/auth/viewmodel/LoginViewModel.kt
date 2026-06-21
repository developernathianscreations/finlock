package com.nc.finlocknc.feature.auth.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nc.finlocknc.feature.auth.repository.AuthRepository

class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    val loginResult = MutableLiveData<Boolean>()

    fun saveMPin(mpin: String) {
        repository.saveMPin(mpin)
    }

    fun login(mpin: String) {
        loginResult.value = repository.validateMPin(mpin)
    }
}