package com.nc.finlocknc.core.common

import android.util.Log

object AppLogger {

    fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    fun e(tag: String, message: String) {
        Log.e(tag, message)
    }

    fun i(tag: String, message: String) {
        Log.i(tag, message)
    }
}