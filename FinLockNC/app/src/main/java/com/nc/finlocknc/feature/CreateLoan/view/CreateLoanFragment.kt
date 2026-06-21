package com.nc.finlocknc.feature.CreateLoan.view

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.nc.finlocknc.R
import com.nc.finlocknc.databinding.FragmentCreateLoanBinding
import com.nc.finlocknc.feature.home.view.HomeActivity
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class CreateLoanFragment : Fragment() {

    private var _binding: FragmentCreateLoanBinding? = null
    private val binding get() = _binding!!

    private var currentStep = 1
    private val totalSteps = 6

    // Document tracking - MANDATORY
    private var deviceImage1Uri: Uri? = null
    private var deviceImage2Uri: Uri? = null
    private var userImageUri: Uri? = null

    // Document tracking - OPTIONAL
    private var panFileUri: Uri? = null
    private var aadharFileUri: Uri? = null
    private var incomeFileUri: Uri? = null
    private var consentSigned = false

    // Step names
    private val stepNames = arrayOf(
        "Customer Details",
        "Address Information",
        "Guarantor Details",
        "Loan Details",
        "Upload Documents",
        "Digital Signature"
    )

    // File picker launchers - MANDATORY
    private val deviceImage1PickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                deviceImage1Uri = result.data?.data
                updateDocumentStatus("device1", true)
            }
        }

    private val deviceImage2PickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                deviceImage2Uri = result.data?.data
                updateDocumentStatus("device2", true)
            }
        }

    private val userImagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                userImageUri = result.data?.data
                updateDocumentStatus("user", true)
            }
        }

    // File picker launchers - OPTIONAL
    private val panPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                panFileUri = result.data?.data
                updateDocumentStatus("pan", true)
            }
        }

    private val aadharPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                aadharFileUri = result.data?.data
                updateDocumentStatus("aadhar", true)
            }
        }

    private val incomePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                incomeFileUri = result.data?.data
                updateDocumentStatus("income", true)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateLoanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        setupDocumentTitles()
        setupDocumentUploads()
        setupConsentListeners()
        setupDatePicker()
        setupSignature()
        setupLoanAmountCalculation()
        setupTenureDropdown()
        setupStepNavigation()
        updateStepIndicator(1)
        showStep(1)
    }

    private fun setupDocumentTitles() {
        // MANDATORY - Device Images
        binding.step5Documents.tileUserImage.tvDocTitle.text = "User Photo"
        binding.step5Documents.tileDeviceImage1.tvDocTitle.text = "Device Image (Front)"
        binding.step5Documents.tileDeviceImage2.tvDocTitle.text = "Device Image (Back)"

        // OPTIONAL - Identity Documents
        binding.step5Documents.tilePan.tvDocTitle.text = "PAN Card"
        binding.step5Documents.tileAadhar.tvDocTitle.text = "Aadhaar Card"
    }

    private fun setupDocumentUploads() {
        // MANDATORY - Device Images
        binding.step5Documents.tileDeviceImage1.root.setOnClickListener {
            openFilePicker(deviceImage1PickerLauncher, "Select Device Image (Front)")
        }
        binding.step5Documents.tileDeviceImage2.root.setOnClickListener {
            openFilePicker(deviceImage2PickerLauncher, "Select Device Image (Back)")
        }
        binding.step5Documents.tileUserImage.root.setOnClickListener {
            openFilePicker(userImagePickerLauncher, "Select User Photo")
        }

        // OPTIONAL - Identity Documents
        binding.step5Documents.tilePan.root.setOnClickListener {
            openFilePicker(panPickerLauncher, "Select PAN Card")
        }
        binding.step5Documents.tileAadhar.root.setOnClickListener {
            openFilePicker(aadharPickerLauncher, "Select Aadhaar Card")
        }
    }

    private fun setupConsentListeners() {
        binding.step5Documents.btnViewConsent.setOnClickListener {
            Toast.makeText(requireContext(), "Opening Consent Document...", Toast.LENGTH_SHORT).show()
        }

        binding.step5Documents.btnSignConsent.setOnClickListener {
            consentSigned = true
            binding.step5Documents.tvConsentStatus.text = "✓ Signed"
            binding.step5Documents.tvConsentStatus.setTextColor(resources.getColor(R.color.green))
            binding.step5Documents.tvConsentStatus.setBackgroundResource(R.drawable.bg_done_badge)
            updateOptionalDocsStatus()
            Toast.makeText(requireContext(), "Consent Signed Successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateDocumentStatus(docType: String, isUploaded: Boolean) {
        val tile = when (docType) {
            "device1" -> binding.step5Documents.tileDeviceImage1
            "device2" -> binding.step5Documents.tileDeviceImage2
            "user" -> binding.step5Documents.tileUserImage
            "pan" -> binding.step5Documents.tilePan
            "aadhar" -> binding.step5Documents.tileAadhar
            else -> null
        }

        tile?.let {
            if (isUploaded) {
                it.tvDocStatus.text = "✓ Uploaded"
                it.tvDocStatus.setTextColor(resources.getColor(R.color.green))
                it.ivDocCheck.visibility = View.VISIBLE
                it.ivDocAction.visibility = View.GONE
            }
        }

        updateMandatoryDocsStatus()
        updateOptionalDocsStatus()
        updateDocsCounter()
    }

    private fun updateMandatoryDocsStatus() {
        val allUploaded = listOf(
            deviceImage1Uri,
            deviceImage2Uri,
            userImageUri
        ).all { it != null }

        if (allUploaded) {
            binding.step5Documents.tvMandatoryDone.visibility = View.VISIBLE
        } else {
            binding.step5Documents.tvMandatoryDone.visibility = View.GONE
        }
    }

    private fun updateOptionalDocsStatus() {
        val allUploaded = listOf(
            panFileUri,
            aadharFileUri
        ).all { it != null } && consentSigned

        if (allUploaded) {
            binding.step5Documents.tvOptionalDone.visibility = View.VISIBLE
        } else {
            binding.step5Documents.tvOptionalDone.visibility = View.GONE
        }
    }

    private fun updateDocsCounter() {
        val mandatoryCount = listOf(
            deviceImage1Uri,
            deviceImage2Uri,
            userImageUri
        ).count { it != null }

        val optionalCount = listOf(
            panFileUri,
            aadharFileUri
        ).count { it != null } + if (consentSigned) 1 else 0

        val totalUploaded = mandatoryCount + optionalCount
        binding.step5Documents.tvDocsCounter.text = "$totalUploaded of 7 completed"
    }

    private fun openFilePicker(launcher: androidx.activity.result.ActivityResultLauncher<Intent>, title: String) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/* application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        launcher.launch(Intent.createChooser(intent, title))
    }

    private fun setupStepNavigation() {
        binding.btnBack.setOnClickListener {
            if (currentStep > 1) {
                currentStep--
                showStep(currentStep)
                updateStepIndicator(currentStep)
            }
        }

        binding.btnNext.setOnClickListener {
            if (validateCurrentStep()) {
                markStepCompleted(currentStep)
                if (currentStep < totalSteps) {
                    currentStep++
                    showStep(currentStep)
                    updateStepIndicator(currentStep)
                } else {
                    submitLoanApplication()
                }
            }
        }
    }

    private fun updateStepIndicator(step: Int) {
        val dotStep1 = view?.findViewById<View>(R.id.dot_step1)
        val dotStep2 = view?.findViewById<View>(R.id.dot_step2)
        val dotStep3 = view?.findViewById<View>(R.id.dot_step3)
        val dotStep4 = view?.findViewById<View>(R.id.dot_step4)
        val dotStep5 = view?.findViewById<View>(R.id.dot_step5)
        val dotStep6 = view?.findViewById<View>(R.id.dot_step6)

        val tvStepLabel = view?.findViewById<TextView>(R.id.tv_step_label)
        val tvStepName = view?.findViewById<TextView>(R.id.tv_step_name)

        tvStepLabel?.text = "Step $step of $totalSteps"
        tvStepName?.text = stepNames[step - 1]

        val dots = listOf(dotStep1, dotStep2, dotStep3, dotStep4, dotStep5, dotStep6)
        dots.forEach { dot ->
            dot?.setBackgroundResource(R.drawable.bg_step_dot_inactive)
        }

        when (step) {
            1 -> dotStep1?.setBackgroundResource(R.drawable.bg_step_dot_active)
            2 -> dotStep2?.setBackgroundResource(R.drawable.bg_step_dot_active)
            3 -> dotStep3?.setBackgroundResource(R.drawable.bg_step_dot_active)
            4 -> dotStep4?.setBackgroundResource(R.drawable.bg_step_dot_active)
            5 -> dotStep5?.setBackgroundResource(R.drawable.bg_step_dot_active)
            6 -> dotStep6?.setBackgroundResource(R.drawable.bg_step_dot_active)
        }

        for (i in 1 until step) {
            when (i) {
                1 -> dotStep1?.setBackgroundResource(R.drawable.bg_step_dot_completed)
                2 -> dotStep2?.setBackgroundResource(R.drawable.bg_step_dot_completed)
                3 -> dotStep3?.setBackgroundResource(R.drawable.bg_step_dot_completed)
                4 -> dotStep4?.setBackgroundResource(R.drawable.bg_step_dot_completed)
                5 -> dotStep5?.setBackgroundResource(R.drawable.bg_step_dot_completed)
                6 -> dotStep6?.setBackgroundResource(R.drawable.bg_step_dot_completed)
            }
        }
    }

    private fun showStep(step: Int) {
        binding.step1Customer.root.visibility = View.GONE
        binding.step2Address.root.visibility = View.GONE
        binding.step3Guarantor.root.visibility = View.GONE
        binding.step4Loan.root.visibility = View.GONE
        binding.step5Documents.root.visibility = View.GONE
        binding.step6Signature.root.visibility = View.GONE

        when (step) {
            1 -> binding.step1Customer.root.visibility = View.VISIBLE
            2 -> binding.step2Address.root.visibility = View.VISIBLE
            3 -> binding.step3Guarantor.root.visibility = View.VISIBLE
            4 -> binding.step4Loan.root.visibility = View.VISIBLE
            5 -> binding.step5Documents.root.visibility = View.VISIBLE
            6 -> binding.step6Signature.root.visibility = View.VISIBLE
        }

        binding.btnBack.visibility = if (step == 1) View.GONE else View.VISIBLE
        binding.btnNext.text = if (step == totalSteps) "Submit Application" else "Continue"

        binding.nestedScrollView.post {
            binding.nestedScrollView.smoothScrollTo(0, 0)
        }
    }

    private fun markStepCompleted(step: Int) {
        when (step) {
            1 -> binding.step1Customer.tvCustomerDone.visibility = View.VISIBLE
            2 -> binding.step2Address.tvAddressDone.visibility = View.VISIBLE
            3 -> binding.step3Guarantor.tvGuarantorDone.visibility = View.VISIBLE
            4 -> binding.step4Loan.tvLoanDone.visibility = View.VISIBLE
            5 -> {
                // Step 5 completion is handled by document uploads
            }
            6 -> binding.step6Signature.tvSigDone.visibility = View.VISIBLE
        }
    }

    private fun validateCurrentStep(): Boolean {
        return when (currentStep) {
            1 -> validateStep1()
            2 -> validateStep2()
            3 -> validateStep3()
            4 -> validateStep4()
            5 -> validateStep5()
            6 -> validateStep6()
            else -> false
        }
    }

    private fun validateStep1(): Boolean {
        var isValid = true

        val customerName = binding.step1Customer.etCustomerName.text.toString().trim()
        if (customerName.isEmpty()) {
            binding.step1Customer.etCustomerName.error = "Customer name is required"
            isValid = false
        } else if (customerName.length < 3) {
            binding.step1Customer.etCustomerName.error = "Name must be at least 3 characters"
            isValid = false
        } else {
            binding.step1Customer.etCustomerName.error = null
        }

        val fatherName = binding.step1Customer.etFatherName.text.toString().trim()
        if (fatherName.isEmpty()) {
            binding.step1Customer.etFatherName.error = "Father name is required"
            isValid = false
        } else {
            binding.step1Customer.etFatherName.error = null
        }

        val mobile = binding.step1Customer.etMobile.text.toString().trim()
        if (mobile.isEmpty()) {
            binding.step1Customer.etMobile.error = "Mobile number is required"
            isValid = false
        } else if (mobile.length != 10 || !mobile.all { it.isDigit() }) {
            binding.step1Customer.etMobile.error = "Enter valid 10-digit mobile number"
            isValid = false
        } else {
            binding.step1Customer.etMobile.error = null
        }

        val email = binding.step1Customer.etEmail.text.toString().trim()
        if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.step1Customer.etEmail.error = "Enter valid email address"
            isValid = false
        } else {
            binding.step1Customer.etEmail.error = null
        }

        if (binding.step1Customer.etDob.text.toString().trim().isEmpty()) {
            binding.step1Customer.etDob.error = "Date of Birth is required"
            isValid = false
        } else {
            binding.step1Customer.etDob.error = null
        }

        if (binding.step1Customer.etOccupation.text.toString().trim().isEmpty()) {
            binding.step1Customer.etOccupation.error = "Occupation is required"
            isValid = false
        } else {
            binding.step1Customer.etOccupation.error = null
        }

        if (isValid) {
            Toast.makeText(requireContext(), "✓ Customer details saved", Toast.LENGTH_SHORT).show()
        }
        return isValid
    }

    private fun validateStep2(): Boolean {
        var isValid = true

        if (binding.step2Address.etAddress.text.toString().trim().isEmpty()) {
            binding.step2Address.etAddress.error = "Address is required"
            isValid = false
        } else {
            binding.step2Address.etAddress.error = null
        }

        if (binding.step2Address.etCity.text.toString().trim().isEmpty()) {
            binding.step2Address.etCity.error = "City is required"
            isValid = false
        } else {
            binding.step2Address.etCity.error = null
        }

        if (binding.step2Address.etState.text.toString().trim().isEmpty()) {
            binding.step2Address.etState.error = "State is required"
            isValid = false
        } else {
            binding.step2Address.etState.error = null
        }

        val pincode = binding.step2Address.etPincode.text.toString().trim()
        if (pincode.isEmpty()) {
            binding.step2Address.etPincode.error = "Pincode is required"
            isValid = false
        } else if (pincode.length != 6 || !pincode.all { it.isDigit() }) {
            binding.step2Address.etPincode.error = "Enter valid 6-digit pincode"
            isValid = false
        } else {
            binding.step2Address.etPincode.error = null
        }

        if (isValid) {
            Toast.makeText(requireContext(), "✓ Address saved", Toast.LENGTH_SHORT).show()
        }
        return isValid
    }

    private fun validateStep3(): Boolean {
        var isValid = true

        if (binding.step3Guarantor.etGuarantorName.text.toString().trim().isEmpty()) {
            binding.step3Guarantor.etGuarantorName.error = "Guarantor name is required"
            isValid = false
        } else {
            binding.step3Guarantor.etGuarantorName.error = null
        }

        val guarantorMobile = binding.step3Guarantor.etGuarantorMobile.text.toString().trim()
        if (guarantorMobile.isEmpty()) {
            binding.step3Guarantor.etGuarantorMobile.error = "Guarantor mobile is required"
            isValid = false
        } else if (guarantorMobile.length != 10 || !guarantorMobile.all { it.isDigit() }) {
            binding.step3Guarantor.etGuarantorMobile.error = "Enter valid 10-digit mobile number"
            isValid = false
        } else {
            binding.step3Guarantor.etGuarantorMobile.error = null
        }

        if (binding.step3Guarantor.etRelationship.text.toString().trim().isEmpty()) {
            binding.step3Guarantor.etRelationship.error = "Relationship is required"
            isValid = false
        } else {
            binding.step3Guarantor.etRelationship.error = null
        }

        if (isValid) {
            Toast.makeText(requireContext(), "✓ Guarantor details saved", Toast.LENGTH_SHORT).show()
        }
        return isValid
    }

    private fun validateStep4(): Boolean {
        var isValid = true

        val loanAmount = binding.step4Loan.etLoanAmount.text.toString().trim()
        if (loanAmount.isEmpty()) {
            binding.step4Loan.etLoanAmount.error = "Loan amount is required"
            isValid = false
        } else if (loanAmount.toIntOrNull() == null || loanAmount.toInt() <= 0) {
            binding.step4Loan.etLoanAmount.error = "Enter valid loan amount"
            isValid = false
        } else {
            binding.step4Loan.etLoanAmount.error = null
        }

        val tenure = binding.step4Loan.etTenure.text.toString().trim()
        if (tenure.isEmpty()) {
            binding.step4Loan.etTenure.error = "Please select tenure"
            isValid = false
        } else {
            binding.step4Loan.etTenure.error = null
        }

        val interestRate = binding.step4Loan.etInterestRate.text.toString().trim()
        if (interestRate.isEmpty()) {
            binding.step4Loan.etInterestRate.error = "Interest rate is required"
            isValid = false
        } else if (interestRate.toDoubleOrNull() == null || interestRate.toDouble() <= 0) {
            binding.step4Loan.etInterestRate.error = "Enter valid interest rate"
            isValid = false
        } else {
            binding.step4Loan.etInterestRate.error = null
        }

        if (isValid) {
            Toast.makeText(requireContext(), "✓ Loan details saved", Toast.LENGTH_SHORT).show()
        }
        return isValid
    }

    private fun validateStep5(): Boolean {
        // MANDATORY - All device images must be uploaded
        if (deviceImage1Uri == null) {
            Toast.makeText(requireContext(), "Please upload Device Image (Front)", Toast.LENGTH_SHORT).show()
            return false
        }

        if (deviceImage2Uri == null) {
            Toast.makeText(requireContext(), "Please upload Device Image (Back)", Toast.LENGTH_SHORT).show()
            return false
        }

        if (userImageUri == null) {
            Toast.makeText(requireContext(), "Please upload User Photo", Toast.LENGTH_SHORT).show()
            return false
        }

        // OPTIONAL - Only show warning if not uploaded
        if (panFileUri == null) {
            Toast.makeText(requireContext(), "⚠️ PAN Card is optional but recommended", Toast.LENGTH_SHORT).show()
        }

        if (aadharFileUri == null) {
            Toast.makeText(requireContext(), "⚠️ Aadhaar Card is optional but recommended", Toast.LENGTH_SHORT).show()
        }

        if (!consentSigned) {
            Toast.makeText(requireContext(), "⚠️ Consent signing is optional but recommended", Toast.LENGTH_SHORT).show()
        }

        return true
    }

    private fun validateStep6(): Boolean {
        if (!binding.step6Signature.signatureView.hasSignature()) {
            Toast.makeText(requireContext(), "Please draw and save your signature", Toast.LENGTH_SHORT).show()
            return false
        }
        Toast.makeText(requireContext(), "✓ Signature saved", Toast.LENGTH_SHORT).show()
        return true
    }

    private fun setupDatePicker() {
        binding.step1Customer.etDob.setOnClickListener {
            showDatePickerDialog()
        }
        binding.step1Customer.etDob.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val formattedDate = String.format("%02d/%02d/%d", day, month + 1, year)
                binding.step1Customer.etDob.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = calendar.timeInMillis
            show()
        }
    }

    private fun setupSignature() {
        binding.step6Signature.signatureView.setOnTouchListener { _, _ ->
            binding.step6Signature.tvSignatureHint.visibility = View.GONE
            false
        }

        binding.step6Signature.btnClearSignature.setOnClickListener {
            binding.step6Signature.signatureView.clear()
            binding.step6Signature.tvSignatureHint.visibility = View.VISIBLE
            binding.step6Signature.tvSignatureStatus.text = "Signature required to proceed"
            binding.step6Signature.tvSigDone.visibility = View.GONE
        }

        binding.step6Signature.btnSaveSignature.setOnClickListener {
            if (binding.step6Signature.signatureView.hasSignature()) {
                saveSignature()
                binding.step6Signature.tvSignatureHint.visibility = View.GONE
                binding.step6Signature.tvSignatureStatus.text = "✓ Signature saved"
                binding.step6Signature.tvSignatureStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                binding.step6Signature.tvSigDone.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Signature saved successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please draw your signature first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveSignature() {
        val signatureBitmap = binding.step6Signature.signatureView.getSignatureBitmap()
        signatureBitmap?.let {
            val file = File(requireContext().cacheDir, "signature_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { outputStream ->
                it.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
        }
    }

    private fun setupLoanAmountCalculation() {
        binding.step4Loan.etLoanAmount.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) calculateAndShowEMI()
        }
        binding.step4Loan.etTenure.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) calculateAndShowEMI()
        }
        binding.step4Loan.etInterestRate.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) calculateAndShowEMI()
        }
    }

    private fun calculateAndShowEMI() {
        val amount = binding.step4Loan.etLoanAmount.text.toString().trim().toDoubleOrNull() ?: 0.0
        val tenure = binding.step4Loan.etTenure.text.toString().trim().toIntOrNull() ?: 0
        val rate = binding.step4Loan.etInterestRate.text.toString().trim().toDoubleOrNull() ?: 0.0

        if (amount > 0 && tenure > 0 && rate > 0) {
            val emi = calculateEMI(amount, rate, tenure)
            val totalInterest = (emi * tenure) - amount
            binding.step4Loan.cardEmiPreview.visibility = View.VISIBLE
            binding.step4Loan.tvEmiAmount.text = String.format("₹ %.0f", emi)
            binding.step4Loan.tvTotalInterest.text = String.format("₹ %.0f", totalInterest)
        }
    }

    private fun submitLoanApplication() {
        val customerName = binding.step1Customer.etCustomerName.text.toString().trim()
        val loanAmount = binding.step4Loan.etLoanAmount.text.toString().trim()
        val tenure = binding.step4Loan.etTenure.text.toString().trim()
        val interestRate = binding.step4Loan.etInterestRate.text.toString().trim()
        val emi = calculateEMI(loanAmount.toDouble(), interestRate.toDouble(), tenure.toInt())

        val message = buildString {
            append("✅ Loan Application Submitted Successfully!\n\n")
            append("Customer: $customerName\n")
            append("Loan Amount: ₹$loanAmount\n")
            append("Tenure: $tenure months\n")
            append("Interest Rate: $interestRate%\n")
            append("EMI: ₹${String.format("%.2f", emi)}\n\n")
            append("Application ID: ${System.currentTimeMillis()}\n")
            append("Status: Pending Approval")
        }

        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        binding.root.postDelayed({
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }, 3000)
    }

    private fun setupTenureDropdown() {
        val tenureList = listOf(
            "3 Months",
            "6 Months",
            "9 Months",
            "12 Months",
            "18 Months",
            "24 Months",
            "36 Months",
            "48 Months",
            "60 Months"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            tenureList
        )

        binding.step4Loan.etTenure.setAdapter(adapter)
        binding.step4Loan.etTenure.setOnClickListener {
            binding.step4Loan.etTenure.showDropDown()
        }

        binding.step4Loan.etTenure.setOnItemClickListener { parent, _, position, _ ->
            val selectedValue = parent.getItemAtPosition(position).toString()
            val months = selectedValue.replace(" Months", "")
            binding.step4Loan.etTenure.setText(months, false)
            binding.step4Loan.etTenure.error = null
            calculateAndShowEMI()
        }
    }

    private fun calculateEMI(principal: Double, rate: Double, tenureInMonths: Int): Double {
        val monthlyRate = rate / (12 * 100)
        val emi = principal * monthlyRate * Math.pow(1 + monthlyRate, tenureInMonths.toDouble()) /
                (Math.pow(1 + monthlyRate, tenureInMonths.toDouble()) - 1)
        return if (emi.isNaN()) 0.0 else emi
    }

    override fun onResume() {
        super.onResume()
        (activity as? HomeActivity)?.hideMainUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? HomeActivity)?.showMainUi()
        _binding = null
    }
}