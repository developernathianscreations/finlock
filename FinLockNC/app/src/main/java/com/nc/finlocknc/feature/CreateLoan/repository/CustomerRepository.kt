package com.nc.finlocknc.feature.CreateLoan.repository

import com.nc.finlocknc.feature.CustomerDetail.model.request.CustomerDetail
import com.nc.finlocknc.feature.CustomerDetail.model.request.DeviceInfo
import com.nc.finlocknc.feature.CustomerDetail.model.request.EMIInfo
import com.nc.finlocknc.feature.CustomerDetail.model.request.FeatureControl
import com.nc.finlocknc.feature.CustomerDetail.model.request.LocationInfo
import com.nc.finlocknc.feature.CustomerDetail.model.request.RecentCommand


class CustomerRepository {

    // In real app, this would fetch from API/Database
/*
    fun getCustomers(): List<Customer> {
        return listOf(
            Customer("Rahul Sharma", "9876543210", "₹5,00,000", "Active", "LN001"),
            Customer("Amit Patil", "9876543211", "₹2,50,000", "Inactive", "LN002"),
            Customer("Priya Singh", "9876543212", "₹7,50,000", "Active", "LN003"),
            Customer("Rohit Kumar", "9876543213", "₹3,20,000", "Inactive", "LN004"),
            Customer("Sneha Patil", "9876543214", "₹8,00,000", "Active", "LN005"),
            Customer("Vikram Mehta", "9876543215", "₹4,50,000", "Active", "LN006"),
            Customer("Neha Gupta", "9876543216", "₹1,80,000", "Inactive", "LN007"),
            Customer("Rajesh Verma", "9876543217", "₹6,20,000", "Active", "LN008")
        )
    }
*/

    fun getCustomerDetail(customerId: String): CustomerDetail {
        // In real app, fetch from API/Database based on customerId
        return CustomerDetail(
            customerName = "Rahul Sharma",
            customerId = customerId,
            mobileNumber = "9876543210",
            loanAmount = "₹5,00,000",
            emiStatus = "Active",
            deviceInfo = DeviceInfo(
                deviceName = "Samsung M35",
                imei = "123456789012345",
                onlineStatus = true,
                lockStatus = "Locked",
                battery = 82,
                lastSeen = "2 min ago"
            ),
            locationInfo = LocationInfo(
                latitude = 18.5204,
                longitude = 73.8567
            ),
            emiInfo = EMIInfo(
                totalLoan = "₹5,00,000",
                paidAmount = "₹2,50,000",
                pendingAmount = "₹2,50,000",
                nextEmiDate = "15 July 2026",
                penalty = "₹500"
            ),
            retailerNotes = "Customer promised payment on 20th July",
            featureControls = listOf(
                FeatureControl("1", "Schedule Lock", true),
                FeatureControl("2", "App Blocking", true),
                FeatureControl("3", "WhatsApp Block", true),
                FeatureControl("4", "Instagram Block", true),
                FeatureControl("5", "YouTube Block", true),
                FeatureControl("6", "Chrome Block", true),
                FeatureControl("7", "Facebook Block", true),
                FeatureControl("8", "Telegram Block", true),
                FeatureControl("9", "USB Disable", true),
                FeatureControl("10", "Factory Reset Block", true),
                FeatureControl("11", "Power Off Restriction", true),
                FeatureControl("12", "Safe Mode Restriction", true),
                FeatureControl("13", "SIM Change Alert", true),
                FeatureControl("14", "Device Location Tracking", true),
                FeatureControl("15", "Lost Mode", true),
                FeatureControl("16", "Retailer Remarks", true),
                FeatureControl("17", "EMI Alerts", true),
                FeatureControl("18", "Auto E-Mandate", true),
                FeatureControl("19", "Shop Advertisement", true),
                FeatureControl("20", "Customer App Access", true)
            ),
            recentCommands = listOf(
                RecentCommand("Lock Device", "Success", "Today, 10:30 AM"),
                RecentCommand("Location Request", "Success", "Today, 10:25 AM"),
                RecentCommand("App Block Applied", "Success", "Today, 10:20 AM")
            )
        )
    }

    // Action methods
    fun lockDevice(customerId: String): Boolean {
        // Implement lock device API call
        return true
    }

    fun unlockDevice(customerId: String): Boolean {
        // Implement unlock device API call
        return true
    }

    fun toggleFeature(customerId: String, featureId: String, enabled: Boolean): Boolean {
        // Implement feature toggle API call
        return true
    }

    fun viewOnMap(latitude: Double, longitude: Double): Boolean {
        // Implement map view intent
        return true
    }
}