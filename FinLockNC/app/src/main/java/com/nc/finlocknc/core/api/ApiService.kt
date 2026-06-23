package com.nc.finlocknc.core.api

import com.nc.finlocknc.feature.CreateLoan.model.request.AddCustomerRequest
import com.nc.finlocknc.feature.CreateLoan.model.response.AddCustomerResponse
import com.nc.finlocknc.feature.CreateLoan.model.response.LicenseKeyResponse
import com.nc.finlocknc.feature.CreateLoan.model.response.UploadResponse
import com.nc.finlocknc.feature.OngoingLoan.model.response.LoanCustomerResponse
import com.nc.finlocknc.feature.auth.model.response.RetailerDetailResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @GET("app-assets/api/get_retailer_details.php")
    suspend fun getCustomerByMobile(
        @Query("mobile") mobile: String
    ): Response<RetailerDetailResponse>


    @GET("app-assets/api/get_loan_customer_by_retailer.php")
    suspend fun getCustomerList(@Query("id") id: String): LoanCustomerResponse

/*
    @GET("paylock/app-assets/api/get_support_team_list.php")
    suspend fun getSupportTeam(): SupportResponse
*/

    @POST("app-assets/api/store_loan_details.php")
    suspend fun addCustomer(
        @Body customerData: AddCustomerRequest
    ): AddCustomerResponse


    @GET("app-assets/api/get_license_key.php")
    suspend fun getLicenseKey(@Query("id") id: Int): Response<LicenseKeyResponse>

    @Multipart
    @POST("app-assets/api/store_image.php")
    suspend fun uploadImage(
        @Part("path") path: RequestBody,
        @Part("type") type: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<UploadResponse>


 /*   @GET("paylock/app-assets/api/lock_unlock_user_device.php")
    suspend fun lockUnlockDeviceRaw(
        @Query("id") id: Int,
        @Query("value") value: Int
    ): Response<ResponseBody>



    @GET("paylock/app-assets/api/emi_record_update.php")
    suspend fun updateEmiRecord(
        @Query("id") id: Int
    ): EmiRecordUpdateResponse

    @POST("paylock/android_mgmt_api/create_policy")
    suspend fun createPolicy(
        @Body request: CreatePolicyRequest
    ): Response<CreatePolicyResponse>

    @POST("paylock/app-assets/api/app_version_check.php") // adjust if needed
    suspend fun checkAppUpdate(): AppUpdateResponse*/


}
