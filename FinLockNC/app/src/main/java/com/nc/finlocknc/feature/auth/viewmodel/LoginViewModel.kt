package com.nc.finlocknc.feature.auth.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nc.finlocknc.core.common.UiState
import com.nc.finlocknc.feature.auth.model.request.RetailerDetail
import com.nc.finlocknc.feature.auth.model.response.RetailerDetailResponse
import com.nc.finlocknc.feature.auth.repository.AuthRepository
import kotlinx.coroutines.launch

    class LoginViewModel(
        private val repository: AuthRepository
    ) : ViewModel() {

        // Login State
        private val _loginState =
            MutableLiveData<UiState<Boolean>>()

        val loginState: LiveData<UiState<Boolean>>
            get() = _loginState

        // Customer State
        private val _customerState =
            MutableLiveData<UiState<RetailerDetailResponse>>()

        val customerState: LiveData<UiState<RetailerDetailResponse>>

            get() = _customerState
        fun saveMPin(mpin: String) {
            repository.saveMPin(mpin)
        }

        fun login(mpin: String) {

            _loginState.value = UiState.Loading

            try {

                val isValid =
                    repository.validateMPin(mpin)

                _loginState.value =
                    UiState.Success(isValid)

            } catch (e: Exception) {

                _loginState.value =
                    UiState.Error(
                        e.message ?: "Login Failed"
                    )
            }
        }

        fun fetchCustomerByMobile(mobile: String) {

            _customerState.value = UiState.Loading

            viewModelScope.launch {

                try {

                    val response =
                        repository.getCustomerByMobile(mobile)

                    if (response.isSuccessful) {

                        val body = response.body()

                        if (
                            body?.status.equals(
                                "success",
                                true
                            )
                        ) {

                            _customerState.value =
                                UiState.Success(
                                    body!!
                                )
                        } else {

                            _customerState.value =
                                UiState.Error(
                                    body?.message
                                        ?: "Something went wrong"
                                )
                        }

                    } else {

                        _customerState.value =
                            UiState.Error(
                                response.message()
                            )
                    }

                } catch (e: Exception) {

                    _customerState.value =
                        UiState.Error(
                            e.message
                                ?: "Network Error"
                        )
                }
            }
        }

        fun resetStates() {

            _loginState.value =
                UiState.Success(false)
        }
    }