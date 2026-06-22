package com.nc.finlocknc.feature.auth.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.InputType
import android.view.KeyEvent
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.nc.finlocknc.R
import com.nc.finlocknc.core.common.UiState
import com.nc.finlocknc.databinding.ActivityLoginBinding
import com.nc.finlocknc.feature.auth.LoginViewModelFactory.LoginViewModelFactory
import com.nc.finlocknc.feature.auth.PrefManager.PrefManager
import com.nc.finlocknc.feature.auth.adapter.BannerSliderAdapter
import com.nc.finlocknc.feature.auth.repository.AuthRepositoryImpl
import com.nc.finlocknc.feature.auth.viewmodel.LoginViewModel
import com.nc.finlocknc.feature.home.view.HomeActivity
import com.nc.finlocknc.feature.service.view.ServiceActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var digitBoxes: Array<EditText>
    private var isPasswordVisible = false

    // Banner images - replace with your actual drawable resources
    private val bannerImages = listOf(
        R.drawable.banner,
        R.drawable.banner,
        R.drawable.banner
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = AuthRepositoryImpl(PrefManager(this))

        viewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(repository)
        )[LoginViewModel::class.java]

        // Set default MPIN for testing (remove in production)
        val prefManager = PrefManager(this)

        if (prefManager.getMPin().isEmpty()) {
            startActivity(
                Intent(
                    this,
                    MobileActivity::class.java
                )
            )
            finish()
            return
        }
        initializeDigitBoxes()
        setupAutoMove()
        setupDeleteHandler()
        setupPasswordToggle()
        setupBannerSlider()
        setupClickListeners()

        // Observe login result
        viewModel.loginState.observe(this) { state ->

            when (state) {

                is UiState.Loading -> {
                    binding.btnLogin.isEnabled = false
                }

                is UiState.Success -> {

                    binding.btnLogin.isEnabled = true

                    if (state.data) {

                        Toast.makeText(
                            this,
                            "Login Successful",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(
                            Intent(
                                this,
                                HomeActivity::class.java
                            )
                        )

                        finish()

                    } else {

                        Toast.makeText(
                            this,
                            "Wrong MPIN. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()

                        clearMpinBoxes()
                    }
                }

                is UiState.Error -> {

                    binding.btnLogin.isEnabled = true

                    Toast.makeText(
                        this,
                        state.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
        private fun initializeDigitBoxes() {
        digitBoxes = arrayOf(
            binding.digit1,
            binding.digit2,
            binding.digit3,
            binding.digit4
        )
        // Set password transformation to show dots by default
        for (box in digitBoxes) {
            box.transformationMethod =
                android.text.method.PasswordTransformationMethod.getInstance()
        }
    }

    private fun setupAutoMove() {
        for (i in digitBoxes.indices) {
            digitBoxes[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && i < digitBoxes.size - 1) {
                        digitBoxes[i + 1].requestFocus()
                    }
                    // Auto login when all 6 digits are entered
                    if (getMpinFromBoxes().length == 4) {
                        val mpin = getMpinFromBoxes()
                        viewModel.login(mpin)
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun setupDeleteHandler() {
        for (i in digitBoxes.indices) {
            digitBoxes[i].setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (digitBoxes[i].text.isEmpty() && i > 0) {
                        digitBoxes[i - 1].requestFocus()
                        digitBoxes[i - 1].setText("")
                    }
                }
                false
            }
        }
    }

    private fun setupPasswordToggle() {
        binding.btnTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            for (box in digitBoxes) {
                if (isPasswordVisible) {
                    // Show digits
                    box.inputType = InputType.TYPE_CLASS_NUMBER
                    box.transformationMethod = null
                    binding.btnTogglePassword.setImageResource(R.drawable.ic_eye_open)
                } else {
                    // Hide digits (show dots)
                    box.inputType =
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                    box.transformationMethod =
                        android.text.method.PasswordTransformationMethod.getInstance()
                    binding.btnTogglePassword.setImageResource(R.drawable.ic_eye_close)
                }
                // Set text again to refresh
                val currentText = box.text.toString()
                box.setText(currentText)
                box.setSelection(box.text.length)
            }
        }
    }

    private fun setupBannerSlider() {
        val adapter = BannerSliderAdapter(bannerImages)
        binding.bannerSlider.adapter = adapter

        // Setup dots indicator
        val dots = arrayOfNulls<TextView>(bannerImages.size)
        for (i in bannerImages.indices) {
            val dot = TextView(this).apply {
                text = "●"
                textSize = 12f
                setTextColor(resources.getColor(R.color.dot_inactive, null))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 0, 8, 0)
                }
            }
            binding.indicatorContainer.addView(dot)
            dots[i] = dot
        }

        // Update dots on page change
        binding.bannerSlider.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                for (i in dots.indices) {
                    dots[i]?.setTextColor(
                        if (i == position) resources.getColor(R.color.dot_active, null)
                        else resources.getColor(R.color.dot_inactive, null)
                    )
                }
            }
        })

        // Set first dot as active
        dots[0]?.setTextColor(resources.getColor(R.color.dot_active, null))
    }

    private fun setupClickListeners() {
        // Login button click
        binding.btnLogin.setOnClickListener {
            val mpin = getMpinFromBoxes()
            if (mpin.length != 4) {
                Toast.makeText(this, "Please enter complete 4-digit MPIN", Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.login(mpin)
            }
        }

        // Forgot MPIN
        binding.tvForgotMpin.setOnClickListener {
            Toast.makeText(this, "Forgot MPIN clicked", Toast.LENGTH_SHORT).show()
            // TODO: Implement forgot MPIN flow
        }

        // Fingerprint login
        binding.llFingerprint.setOnClickListener {
            Toast.makeText(this, "Fingerprint authentication", Toast.LENGTH_SHORT).show()
            // TODO: Implement biometric authentication
        }

        // Support button
        binding.tvSupport.setOnClickListener {

            startActivity(
                Intent(
                    this@LoginActivity,
                    ServiceActivity::class.java
                )
            )
        }
        /*
        // Terms & Conditions button
        binding.btnTerms.setOnClickListener {
            Toast.makeText(this, "Terms & Conditions clicked", Toast.LENGTH_SHORT).show()
            // TODO: Open terms and conditions
        }

*/
        // Bottom menu items
        /*
        binding.bottomApplyNow.setOnClickListener {
            Toast.makeText(this, "Apply Now clicked", Toast.LENGTH_SHORT).show()
        }

        binding.bottomSupport.setOnClickListener {
            Toast.makeText(this, "Support clicked", Toast.LENGTH_SHORT).show()
        }

        binding.bottomViewBalance.setOnClickListener {
            Toast.makeText(this, "View Balance clicked", Toast.LENGTH_SHORT).show()
        }

        binding.bottomGrabDeals.setOnClickListener {
            Toast.makeText(this, "Grab deals clicked", Toast.LENGTH_SHORT).show()
        }

        binding.bottomMore.setOnClickListener {
            Toast.makeText(this, "More options clicked", Toast.LENGTH_SHORT).show()
        }

        // Top links
        binding.tvSecurityTips.setOnClickListener {
            Toast.makeText(this, "Security Tips clicked", Toast.LENGTH_SHORT).show()
*/


        binding.tvPrivacyPolicy.setOnClickListener {
            Toast.makeText(this, "Privacy Policy clicked", Toast.LENGTH_SHORT).show()
        }

        binding.tvNotYou.setOnClickListener {
            Toast.makeText(this, "Not You? clicked", Toast.LENGTH_SHORT).show()
            // TODO: Implement logout or switch account
        }
    }

    private fun getMpinFromBoxes(): String {
        val mpin = StringBuilder()
        for (box in digitBoxes) {
            mpin.append(box.text.toString())
        }
        return mpin.toString()
    }

    private fun clearMpinBoxes() {
        for (box in digitBoxes) {
            box.setText("")
        }
        digitBoxes[0].requestFocus()
    }
}