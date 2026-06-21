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
}