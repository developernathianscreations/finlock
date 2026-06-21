package com.nc.finlocknc.feature.CustomerDetail.model.request




data class CustomerDetail(
    val customerName: String,
    val customerId: String,
    val mobileNumber: String,
    val loanAmount: String,
    val emiStatus: String,

    val deviceInfo: DeviceInfo,
    val locationInfo: LocationInfo,
    val emiInfo: EMIInfo,

    val retailerNotes: String,

    val featureControls: List<FeatureControl>,
    val recentCommands: List<RecentCommand>
)
data class DeviceInfo(
    val deviceName: String,
    val imei: String,

    val onlineStatus: Boolean,
    val lockStatus: String,

    val battery: Int,
    val lastSeen: String
)
data class LocationInfo(
    val latitude: Double,
    val longitude: Double
)
data class EMIInfo(
    val totalLoan: String,
    val paidAmount: String,
    val pendingAmount: String,

    val nextEmiDate: String,
    val penalty: String
)
data class FeatureControl(
    val id: String,
    val featureName: String,
    val isEnabled: Boolean
)

data class RecentCommand(
    val commandName: String,
    val status: String,
    val time: String
)