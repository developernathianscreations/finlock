package com.nc.finlocknc.feature.privacy.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.nc.finlocknc.R

class PrivacyFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_privacy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupContactSupportButton(view)
    }

    /**
     * Setup click listener for the Contact Support button
     */
    private fun setupContactSupportButton(view: View) {
        val contactButton = view.findViewById<MaterialButton>(R.id.contactSupportButton)
        contactButton?.setOnClickListener {
            showContactOptionsDialog()
        }
    }

    /**
     * Show contact options dialog with Email and Call options
     */
    private fun showContactOptionsDialog() {
        val options = arrayOf("Send Email", "Call Support", "Visit Website", "Cancel")

        AlertDialog.Builder(requireContext())
            .setTitle("Contact Finlock NC Support")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openEmail()
                    1 -> openDialer()
                    2 -> openWebsite()
                    else -> { /* Cancel - do nothing */ }
                }
            }
            .show()
    }

    /**
     * Open email client with support email
     */
    private fun openEmail() {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@finlocknc.com")
                putExtra(Intent.EXTRA_SUBJECT, "Privacy Policy Support Request - Finlock NC")
                putExtra(Intent.EXTRA_TEXT, "Dear Finlock NC Support Team,\n\nI have a question regarding the Privacy Policy:\n\n")
            }
            startActivity(Intent.createChooser(intent, "Send Email to Support"))
        } catch (e: Exception) {
            showErrorDialog("No email app found. Please email us at support@finlocknc.com")
        }
    }

    /**
     * Open dialer with support phone number
     */
    private fun openDialer() {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:+18005550199")
            }
            startActivity(intent)
        } catch (e: Exception) {
            showErrorDialog("No dialer app found. Please call +1-800-555-0199")
        }
    }

    /**
     * Open website in browser
     */
    private fun openWebsite() {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.finlocknc.com")
            }
            startActivity(intent)
        } catch (e: Exception) {
            showErrorDialog("No browser app found. Please visit www.finlocknc.com")
        }
    }

    /**
     * Show error dialog when action cannot be performed
     */
    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Action Unavailable")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = PrivacyFragment()
    }
}