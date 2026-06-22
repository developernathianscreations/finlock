package com.nc.finlocknc.feature.home.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.nc.finlocknc.R

import com.nc.finlocknc.databinding.ActivityHomeBinding
import com.nc.finlocknc.feature.CreateLoan.view.CreateLoanFragment
import com.nc.finlocknc.feature.OngoingLoan.model.request.CustomerLoanData
import com.nc.finlocknc.feature.auth.view.LoginActivity
import com.nc.finlocknc.feature.profile.view.ProfileFragment
import com.nc.finlocknc.feature.service.view.ServiceActivity
import com.nc.finlocknc.feature.service.view.ServiceFragment
import com.nc.finlocknc.feature.settings.view.SettingsFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var backPressedTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupDrawer()

        loadFragment(DashboardFragment())

        binding.bottomNavigation.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.nav_dashboard -> {
                    loadFragment(DashboardFragment())
                    true
                }

                R.id.nav_service -> {
                    loadFragment(CreateLoanFragment())
                    true
                }

                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }

                else -> false
            }
        }

        binding.navigationView.setNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.menu_profile -> {

                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameContainer,
                            ProfileFragment()
                        )
                        .addToBackStack("profile")
                        .commit()
                }
                R.id.menu_profile -> {
                    loadFragment(ProfileFragment())
                }
                R.id.menu_settings -> {

                    binding.bottomNavigation.selectedItemId =
                        R.id.nav_settings

                    loadFragment(SettingsFragment())
                }                R.id.menu_support -> {

                    startActivity(
                        Intent(
                            this,
                            ServiceActivity::class.java
                        )
                    )
                }
                R.id.menu_chat -> {

                    try {

                        val phoneNumber = "917709731035"

                        val message =
                            "Hello FinLock Support, I need assistance."

                        val url =
                            "https://wa.me/$phoneNumber?text=" +
                                    java.net.URLEncoder.encode(
                                        message,
                                        "UTF-8"
                                    )

                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                android.net.Uri.parse(url)
                            )
                        )

                    } catch (e: Exception) {

                        Toast.makeText(
                            this,
                            "Unable to open WhatsApp",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                R.id.menu_logout -> {

                    startActivity(
                        Intent(
                            this,
                            LoginActivity::class.java
                        ).apply {
                            flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                    )

                    finish()
                }            }

            binding.drawerLayout.closeDrawers()

            true
        }
    }
    fun navigateToCustomerDetail(loan: CustomerLoanData) {

        hideMainUi()

        val fragment =
            com.nc.finlocknc.feature.CustomerDetail.view.CustomerDetailFragment
                .newInstance(loan)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .addToBackStack("customer_detail")
            .commit()
    }    private fun setupDrawer() {

        binding.toolbar.setNavigationIcon(R.drawable.ic_menu)

        binding.toolbar.setNavigationOnClickListener {

            if (binding.drawerLayout.isDrawerOpen(binding.navigationView)) {
                binding.drawerLayout.closeDrawer(binding.navigationView)
            } else {
                binding.drawerLayout.openDrawer(binding.navigationView)
            }
        }
    }
    override fun onBackPressed() {

        if (supportFragmentManager.backStackEntryCount > 0) {

            showMainUi()

            supportFragmentManager.popBackStack()

            return
        }

        if (System.currentTimeMillis() - backPressedTime < 2000) {

            finishAffinity()
            return
        }

        backPressedTime = System.currentTimeMillis()

        Toast.makeText(
            this,
            "Press back again to exit",
            Toast.LENGTH_SHORT
        ).show()
    }
    private fun loadFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .commit()
    }
    fun hideMainUi() {
        binding.toolbar.visibility = View.GONE
        binding.bottomNavigation.visibility = View.GONE
    }

    fun showMainUi() {
        binding.toolbar.visibility = View.VISIBLE
        binding.bottomNavigation.visibility = View.VISIBLE
    }
}