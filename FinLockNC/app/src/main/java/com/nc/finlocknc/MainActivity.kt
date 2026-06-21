package com.nc.finlocknc

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nc.finlocknc.feature.auth.PrefManager.PrefManager
import com.nc.finlocknc.feature.auth.view.LoginActivity
import com.nc.finlocknc.feature.auth.view.MobileActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefManager = PrefManager(this)

        if (prefManager.isLoggedIn()) {

            startActivity(
                Intent(this, LoginActivity::class.java)
            )

        } else {

            startActivity(
                Intent(this, MobileActivity::class.java)
            )
        }

        finish()
    }
}