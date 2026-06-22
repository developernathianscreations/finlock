package com.nc.finlocknc.feature.OngoingLoan.model.request

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class CustomerLoanData(
    val id: Int,
    val name: String?,
    val email: String?,
    val contact: String?,
    val pan: String?,
    val aadhar: String?,
    val guaranator_name: String?,
    val guaranator_mobile: String?,
    val product_amount: String?,
    val principal_amount: String?,
    val no_of_emi: Int,
    val financer_bank: String?,
    val down_payment: String?,
    val balance_amount: String?,
    val emi_amount: String?,
    val interest_rate: String?,
    val net_amount: String?,
    val description: String?,
    val mobile_brand: String?,
    val mobile_model: String?,
    val device_type: String?,
    val imei: String?,
    val imeiII: String?,
    val license_key: String?,
    val status: String?,
    var mobile_status: Int,
    val pan_file: String?,
    val aadhar_file: String?,
    val consent_file: String?,
    val customer_sign: String?,
    val customer_profile_pic: String?,
    val retailer_key: Int,
    val created_at: String?,
    val updated_at: String?,
    var emi_status: String?,
    val tenure_months: Int? = 0,
    val paid_months: Int? = 0,
    val next_emi_date: String? = null
) : Parcelable, Serializable {  // ✅ Add Serializable here
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(contact)
        parcel.writeString(pan)
        parcel.writeString(aadhar)
        parcel.writeString(guaranator_name)
        parcel.writeString(guaranator_mobile)
        parcel.writeString(product_amount)
        parcel.writeString(principal_amount)
        parcel.writeInt(no_of_emi)
        parcel.writeString(financer_bank)
        parcel.writeString(down_payment)
        parcel.writeString(balance_amount)
        parcel.writeString(emi_amount)
        parcel.writeString(interest_rate)
        parcel.writeString(net_amount)
        parcel.writeString(description)
        parcel.writeString(mobile_brand)
        parcel.writeString(mobile_model)
        parcel.writeString(device_type)
        parcel.writeString(imei)
        parcel.writeString(imeiII)
        parcel.writeString(license_key)
        parcel.writeString(status)
        parcel.writeInt(mobile_status)
        parcel.writeString(pan_file)
        parcel.writeString(aadhar_file)
        parcel.writeString(consent_file)
        parcel.writeString(customer_sign)
        parcel.writeString(customer_profile_pic)
        parcel.writeInt(retailer_key)
        parcel.writeString(created_at)
        parcel.writeString(updated_at)
        parcel.writeString(emi_status)
        parcel.writeInt(tenure_months ?: 0)
        parcel.writeInt(paid_months ?: 0)
        parcel.writeString(next_emi_date)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<CustomerLoanData> {
        override fun createFromParcel(parcel: Parcel): CustomerLoanData {
            return CustomerLoanData(parcel)
        }

        override fun newArray(size: Int): Array<CustomerLoanData?> {
            return arrayOfNulls(size)
        }
    }
}