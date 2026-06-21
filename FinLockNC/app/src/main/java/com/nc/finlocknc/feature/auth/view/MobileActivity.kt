package com.nc.finlocknc.feature.auth.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nc.finlocknc.R
import com.nc.finlocknc.databinding.ActivityMobileBinding
import com.nc.finlocknc.feature.auth.PrefManager.PrefManager


class MobileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMobileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMobileBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnContinue.setOnClickListener {

            val mobile = binding.etMobile.text.toString()

            if (mobile.length != 10) {

                Toast.makeText(
                    this,
                    "Enter valid mobile number",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                PrefManager(this).saveMobile(mobile)

                startActivity(
                    Intent(
                        this,
                        SetMPinActivity::class.java
                    )
                )

                finish()
            }
        }
    }
}