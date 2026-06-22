package com.nc.finlocknc.feature.CreateLoan.repository

import android.webkit.MimeTypeMap
import com.nc.finlocknc.core.api.RetrofitClient
import com.nc.finlocknc.feature.CreateLoan.model.response.UploadResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class UploadRepository {

    suspend fun uploadImage(
        licenseKey: String,
        type: String,
        imageFile: File
    ): Response<UploadResponse> {
        // Ensure no leading/trailing slashes in path
        val basePath = "retailer/assets/loan_doc/$licenseKey".trimStart('/').trimEnd('/')

        val typePart = RequestBody.create("text/plain".toMediaTypeOrNull(), type)
        val pathPart = RequestBody.create("text/plain".toMediaTypeOrNull(), basePath)

        val mimeType = getMimeType(imageFile)
        val imageRequestBody = RequestBody.create(mimeType.toMediaTypeOrNull(), imageFile)
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)

        return RetrofitClient.apiService.uploadImage(pathPart, typePart, imagePart)
    }

    private fun getMimeType(file: File): String {
        val extension = file.extension.lowercase()
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            ?: when (extension) {
                "jpg", "jpeg" -> "image/jpeg"
                "png" -> "image/png"
                "pdf" -> "application/pdf"
                else -> "application/octet-stream"
            }
    }
}
