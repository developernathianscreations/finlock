package com.nc.finlocknc.feature.CreateLoan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nc.finlocknc.feature.CreateLoan.model.response.LicenseKeyResponse
import com.nc.finlocknc.feature.CreateLoan.repository.LicenseRepository
import kotlinx.coroutines.launch

class LicenseViewModel : ViewModel() {

    private val repository =
        LicenseRepository()

    private val _licenseKey =
        MutableLiveData<LicenseKeyResponse>()

    val licenseKey: LiveData<LicenseKeyResponse> =
        _licenseKey

    fun getLicenseKey(id: Int) {

        viewModelScope.launch {

            val response =
                repository.fetchLicenseKey(id)

            if (response.isSuccessful) {

                response.body()?.let {
                    _licenseKey.value = it
                }
            }
        }
    }
}