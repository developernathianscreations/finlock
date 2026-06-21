package com.nc.finlocknc.feature.CustomerDetail.viewmodel



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nc.finlocknc.feature.CustomerList.repository.CustomerRepository
import com.nc.finlocknc.feature.CustomerDetail.model.request.CustomerDetail
import kotlinx.coroutines.launch

class CustomerDetailViewModel : ViewModel() {

    private val repository = CustomerRepository()

    private val _customerDetail = MutableLiveData<CustomerDetail>()
    val customerDetail: LiveData<CustomerDetail> = _customerDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLocking = MutableLiveData<Boolean>()
    val isLocking: LiveData<Boolean> = _isLocking

    private val _isUnlocking = MutableLiveData<Boolean>()
    val isUnlocking: LiveData<Boolean> = _isUnlocking

    private val _isTogglingFeature = MutableLiveData<Boolean>()
    val isTogglingFeature: LiveData<Boolean> = _isTogglingFeature

    private val _actionMessage = MutableLiveData<String>()
    val actionMessage: LiveData<String> = _actionMessage

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var currentCustomerId: String = ""

    fun loadCustomerDetail(customerId: String) {
        currentCustomerId = customerId
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val detail = repository.getCustomerDetail(customerId)
                _customerDetail.value = detail
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to load customer details: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun lockDevice() {
        viewModelScope.launch {
            try {
                _isLocking.value = true
                val success = repository.lockDevice(currentCustomerId)
                if (success) {
                    _actionMessage.value = "Device locked successfully"
                    // Update local state
                    val current = _customerDetail.value
                    current?.let {
                        val updatedDevice = it.deviceInfo.copy(lockStatus = "Locked")
                        _customerDetail.value = it.copy(deviceInfo = updatedDevice)
                    }
                } else {
                    _error.value = "Failed to lock device"
                }
                _isLocking.value = false
            } catch (e: Exception) {
                _error.value = "Error locking device: ${e.message}"
                _isLocking.value = false
            }
        }
    }

    fun unlockDevice() {
        viewModelScope.launch {
            try {
                _isUnlocking.value = true
                val success = repository.unlockDevice(currentCustomerId)
                if (success) {
                    _actionMessage.value = "Device unlocked successfully"
                    // Update local state
                    val current = _customerDetail.value
                    current?.let {
                        val updatedDevice = it.deviceInfo.copy(lockStatus = "Unlocked")
                        _customerDetail.value = it.copy(deviceInfo = updatedDevice)
                    }
                } else {
                    _error.value = "Failed to unlock device"
                }
                _isUnlocking.value = false
            } catch (e: Exception) {
                _error.value = "Error unlocking device: ${e.message}"
                _isUnlocking.value = false
            }
        }
    }

    fun toggleFeature(featureId: String, enabled: Boolean) {
        viewModelScope.launch {
            try {
                _isTogglingFeature.value = true
                val success = repository.toggleFeature(currentCustomerId, featureId, enabled)
                if (success) {
                    // Update local state
                    val current = _customerDetail.value
                    current?.let {
                        val updatedFeatures = it.featureControls.map { feature ->
                            if (feature.id == featureId) {
                                feature.copy(isEnabled = enabled)
                            } else {
                                feature
                            }
                        }
                        _customerDetail.value = it.copy(featureControls = updatedFeatures)
                     //   _actionMessage.value = "${if (enabled) "Enabled" : "Disabled"} feature"
                    }
                } else {
                    _error.value = "Failed to toggle feature"
                }
                _isTogglingFeature.value = false
            } catch (e: Exception) {
                _error.value = "Error toggling feature: ${e.message}"
                _isTogglingFeature.value = false
            }
        }
    }

    fun viewOnMap() {
        val detail = _customerDetail.value
        detail?.let {
            repository.viewOnMap(it.locationInfo.latitude, it.locationInfo.longitude)
            _actionMessage.value = "Opening map..."
        } ?: run {
            _error.value = "Location information not available"
        }
    }
}