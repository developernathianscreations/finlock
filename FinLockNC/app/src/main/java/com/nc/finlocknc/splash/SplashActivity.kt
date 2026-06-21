package com.nc.finlocknc.splash


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.nc.finlocknc.feature.auth.view.LoginActivity
import com.nc.finlocknc.feature.auth.view.MobileActivity
import com.nc.finlocknc.databinding.ActivitySplashBinding
import com.nc.finlocknc.feature.auth.PrefManager.PrefManager

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({

            val prefManager = PrefManager(this)

            if (prefManager.getMPin().isEmpty()) {

                startActivity(
                    Intent(
                        this,
                        MobileActivity::class.java
                    )
                )

            } else {

                startActivity(
                    Intent(
                        this,
                        LoginActivity::class.java
                    )
                )
            }

            finish()

        }, 2500)
    }
}