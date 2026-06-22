package com.nc.finlocknc.feature.auth.PrefManager


import android.content.Context



class PrefManager(context: Context) {

    private val pref =
        context.getSharedPreferences(
            "finlock_pref",
            Context.MODE_PRIVATE
        )

    fun saveMobile(mobile: String) {
        pref.edit().putString("mobile", mobile).apply()
    }

    fun getMobile(): String {
        return pref.getString("mobile", "") ?: ""
    }

    fun saveMPin(mpin: String) {
        pref.edit().putString("mpin", mpin).apply()
    }

    fun getMPin(): String {
        return pref.getString("mpin", "") ?: ""
    }

    fun isLoggedIn(): Boolean {
        return getMPin().isNotEmpty()
    }

    fun saveRetailerName(name: String) {
        pref.edit().putString("retailer_name", name).apply()
    }

    fun getRetailerName(): String {
        return pref.getString("retailer_name", "") ?: ""
    }
    fun saveRetailerId(id: Int) {
        pref.edit().putInt("retailer_id", id).apply()
    }

    fun getRetailerId(): Int {
        return pref.getInt("retailer_id", 0)
    }

    fun saveRetailerEmail(email: String) {
        pref.edit().putString("retailer_email", email).apply()
    }

    fun getRetailerEmail(): String {
        return pref.getString("retailer_email", "") ?: ""
    }

    fun saveRetailerCity(city: String) {
        pref.edit().putString("retailer_city", city).apply()
    }

    fun getRetailerCity(): String {
        return pref.getString("retailer_city", "") ?: ""
    }

    fun saveRetailerAddress(address: String) {
        pref.edit().putString("retailer_address", address).apply()
    }

    fun getRetailerAddress(): String {
        return pref.getString("retailer_address", "") ?: ""
    }
    // ✅ NEW: License Key
    fun saveLicenseKey(key: String) {
        pref.edit().putString("license_key", key).apply()
    }

    fun getLicenseKey(): String {
        return pref.getString("license_key", "") ?: ""
    }

    fun saveRetailerState(state: String) {
        pref.edit().putString("retailer_state", state).apply()
    }

    fun getRetailerState(): String {
        return pref.getString("retailer_state", "") ?: ""
    }

    fun savePolicyName(policy: String) {
        pref.edit().putString("policy_name", policy).apply()
    }

    fun getPolicyName(): String {
        return pref.getString("policy_name", "") ?: ""
    }

    fun saveActiveStatus(status: Int) {
        pref.edit().putInt("active_status", status).apply()
    }

    fun getActiveStatus(): Int {
        return pref.getInt("active_status", 0)
    }
    fun saveTotalLoans(value: String) {
        pref.edit().putString("total_loans", value).apply()
    }

    fun getTotalLoans(): String {
        return pref.getString("total_loans", "0") ?: "0"
    }

    fun saveOpenLoans(value: String) {
        pref.edit().putString("open_loans", value).apply()
    }

    fun getOpenLoans(): String {
        return pref.getString("open_loans", "0") ?: "0"
    }

    fun saveClosedLoans(value: String) {
        pref.edit().putString("closed_loans", value).apply()
    }

    fun getClosedLoans(): String {
        return pref.getString("closed_loans", "0") ?: "0"
    }

    fun saveTotalKeys(value: String) {
        pref.edit().putString("total_keys", value).apply()
    }

    fun getTotalKeys(): String {
        return pref.getString("total_keys", "0") ?: "0"
    }

    fun saveAssignedKeys(value: String) {
        pref.edit().putString("assigned_keys", value).apply()
    }

    fun getAssignedKeys(): String {
        return pref.getString("assigned_keys", "0") ?: "0"
    }

    fun saveUsedKeys(value: String) {
        pref.edit().putString("used_keys", value).apply()
    }

    fun getUsedKeys(): String {
        return pref.getString("used_keys", "0") ?: "0"
    }
}