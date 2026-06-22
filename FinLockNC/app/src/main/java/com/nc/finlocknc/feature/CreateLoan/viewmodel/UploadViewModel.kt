package com.nc.finlocknc.feature.CreateLoan.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nc.finlocknc.feature.CreateLoan.model.request.UploadResult
import com.nc.finlocknc.feature.CreateLoan.repository.UploadRepository
import kotlinx.coroutines.launch
import java.io.File

class UploadViewModel : ViewModel() {

    private val repository = UploadRepository()

    val uploadResponse = MutableLiveData<Result<UploadResult>>() // ✅ Change type to UploadResult

    fun uploadImage(licenseKey: String, type: String, imageFile: File) {
        viewModelScope.launch {
            try {
                val response = repository.uploadImage(licenseKey, type, imageFile)
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        val cleanUrl = body.url.replace(Regex("(?<!:)//+"), "/")
                        val cleanedResponse = body.copy(url = cleanUrl)

                        val result = UploadResult(type, cleanedResponse) // ✅ wrap with type
                        uploadResponse.postValue(Result.success(result))
                    } ?: uploadResponse.postValue(Result.failure(Throwable("Empty Response")))
                } else {
                    uploadResponse.postValue(Result.failure(Throwable("Error: ${response.code()}")))
                }
            } catch (e: Exception) {
                uploadResponse.postValue(Result.failure(e))
            }
        }
    }
}
