package com.nc.finlocknc.feature.OngoingLoan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nc.finlocknc.feature.OngoingLoan.model.response.LoanCustomerResponse
import com.nc.finlocknc.feature.OngoingLoan.repository.CustomerListRepository
import kotlinx.coroutines.launch

class CustomerLoanListViewModel : ViewModel() {

    private val repository = CustomerListRepository()

    private val _customerLoadData =
        MutableLiveData<LoanCustomerResponse>()

    val customerLoadData: LiveData<LoanCustomerResponse> =
        _customerLoadData

    private val _loading =
        MutableLiveData<Boolean>()

    val loading: LiveData<Boolean> =
        _loading

    private val _error =
        MutableLiveData<String>()

    val error: LiveData<String> =
        _error

    fun fetchCustomerList(id: String) {

        _loading.value = true

        viewModelScope.launch {

            try {

                val response =
                    repository.getCustomerList(id)

                _customerLoadData.value =
                    response

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Something went wrong"

            } finally {

                _loading.value = false
            }
        }
    }

    fun resetState() {

        _loading.value = false
        _error.value = ""
    }
}