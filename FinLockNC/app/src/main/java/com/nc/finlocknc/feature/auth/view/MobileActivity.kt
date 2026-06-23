package com.nc.finlocknc.feature.auth.view

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.nc.finlocknc.core.common.UiState
import com.nc.finlocknc.databinding.ActivityMobileBinding
import com.nc.finlocknc.feature.auth.LoginViewModelFactory.LoginViewModelFactory
import com.nc.finlocknc.feature.auth.PrefManager.PrefManager
import com.nc.finlocknc.feature.auth.repository.AuthRepositoryImpl
import com.nc.finlocknc.feature.auth.viewmodel.LoginViewModel
import android.Manifest
class MobileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMobileBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMobileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestAppPermissions()
        val repository = AuthRepositoryImpl(
            PrefManager(this)
        )

        viewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(repository)
        )[LoginViewModel::class.java]

        setupObservers()
        setupClicks()
    }

    private fun setupClicks() {

        binding.btnContinue.setOnClickListener {

            val mobile =
                binding.etMobile.text.toString().trim()

            when {

                mobile.isEmpty() -> {

                    Toast.makeText(
                        this,
                        "Enter mobile number",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                mobile.length != 10 -> {

                    Toast.makeText(
                        this,
                        "Enter valid mobile number",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {

                    viewModel.fetchCustomerByMobile(
                        mobile
                    )
                }
            }
        }
    }

    private fun setupObservers() {

        viewModel.customerState.observe(this) { state ->

            when (state) {

                is UiState.Loading -> {

                    binding.btnContinue.isEnabled =
                        false

                    if (::binding.isInitialized &&
                        binding.root.findViewById<View?>(
                            resources.getIdentifier(
                                "progressBar",
                                "id",
                                packageName
                            )
                        ) != null
                    ) {

                        binding.progressBar.visibility =
                            View.VISIBLE
                    }
                }

                is UiState.Success -> {

                    binding.btnContinue.isEnabled = true

                    if (::binding.isInitialized &&
                        binding.root.findViewById<View?>(
                            resources.getIdentifier(
                                "progressBar",
                                "id",
                                packageName
                            )
                        ) != null
                    ) {

                        binding.progressBar.visibility =
                            View.GONE
                    }

                    val responseData = state.data

                    val retailer = responseData.retailer
                    val keys = responseData.keys
                    val loans = responseData.loans

                    if (retailer.active_status == 0) {

                        Toast.makeText(
                            this,
                            "Account is inactive",
                            Toast.LENGTH_LONG
                        ).show()

                        return@observe
                    }
                    val prefManager =
                        PrefManager(this)
                    prefManager.saveRetailerId(
                        retailer.id
                    )
                    // Save Mobile
                    prefManager.saveMobile(
                        retailer.mobile ?: ""
                    )

                    // Save Retailer Name
                    prefManager.saveRetailerName(
                        retailer.name ?: ""
                    )
                    prefManager.saveRetailerEmail(
                        retailer.email ?: ""
                    )

                    prefManager.saveMobile(
                        retailer.mobile ?: ""
                    )

                    prefManager.saveRetailerCity(
                        retailer.city ?: ""
                    )

                    prefManager.saveRetailerAddress(
                        retailer.address ?: ""
                    )

                    prefManager.saveRetailerState(
                        retailer.state ?: ""
                    )

                    prefManager.savePolicyName(
                        retailer.policyname ?: ""
                    )

                    prefManager.saveActiveStatus(
                        retailer.active_status
                    )
                    prefManager.saveTotalKeys(
                        keys.total_keys.toString()
                    )

                    prefManager.saveAssignedKeys(
                        keys.assigned_keys
                    )

                    prefManager.saveUsedKeys(
                        keys.used_keys
                    )

                    prefManager.saveTotalLoans(
                        loans.total_loans.toString()
                    )

                    prefManager.saveOpenLoans(
                        loans.open_loans
                    )

                    prefManager.saveClosedLoans(
                        loans.closed_loans
                    )

                    startActivity(
                        Intent(
                            this,
                            SetMPinActivity::class.java
                        )
                    )

                    finish()
                }

                is UiState.Error -> {

                    binding.btnContinue.isEnabled =
                        true

                    if (::binding.isInitialized &&
                        binding.root.findViewById<View?>(
                            resources.getIdentifier(
                                "progressBar",
                                "id",
                                packageName
                            )
                        ) != null
                    ) {

                        binding.progressBar.visibility =
                            View.GONE
                    }

                    Toast.makeText(
                        this,
                        state.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    private fun requestAppPermissions() {

        val permissions =
            mutableListOf<String>()

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            permissions.add(
                Manifest.permission.CAMERA
            )
        }

        if (
            android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.TIRAMISU
        ) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                permissions.add(
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            }

        } else {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                permissions.add(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }

        if (permissions.isNotEmpty()) {

            permissionLauncher.launch(
                permissions.toTypedArray()
            )
        }
    }
    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val cameraGranted =
                permissions[Manifest.permission.CAMERA] ?: false

            if (cameraGranted) {

                Toast.makeText(
                    this,
                    "Permissions Granted",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
}