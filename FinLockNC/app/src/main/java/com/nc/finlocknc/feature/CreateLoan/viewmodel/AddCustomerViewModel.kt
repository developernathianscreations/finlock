package com.nc.finlocknc.feature.CreateLoan.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nc.finlocknc.feature.CreateLoan.model.request.AddCustomerRequest
import com.nc.finlocknc.feature.CreateLoan.model.response.AddCustomerResponse
import com.nc.finlocknc.feature.CreateLoan.repository.CustomerRepository
import kotlinx.coroutines.launch

class AddCustomerViewModel : ViewModel() {

    private val repository = CustomerRepository()

    private val _customerPostData =
        MutableLiveData<AddCustomerResponse>()

    val customerPostData: MutableLiveData<AddCustomerResponse>
        get() = _customerPostData

    private val _error =
        MutableLiveData<String>()

    val error: MutableLiveData<String>
        get() = _error

    private val _isLoading =
        MutableLiveData<Boolean>()

    val isLoading: MutableLiveData<Boolean>
        get() = _isLoading

    fun postCustomer(request: AddCustomerRequest) {

        _isLoading.value = true

        viewModelScope.launch {

            try {

                val response =
                    repository.addCustomer(request)

                _customerPostData.value =
                    response

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Unknown Error"

            } finally {

                _isLoading.value = false
            }
        }
    }
}