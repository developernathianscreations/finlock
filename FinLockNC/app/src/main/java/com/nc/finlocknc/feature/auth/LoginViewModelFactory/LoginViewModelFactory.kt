package com.nc.finlocknc.feature.auth.LoginViewModelFactory


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nc.finlocknc.feature.auth.repository.AuthRepository
import com.nc.finlocknc.feature.auth.viewmodel.LoginViewModel

class LoginViewModelFactory(
    private val repository: AuthRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return LoginViewModel(repository) as T
    }
}