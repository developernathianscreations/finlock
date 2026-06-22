package com.nc.finlocknc.feature.CreateLoan.view

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.nc.finlocknc.R
import com.nc.finlocknc.databinding.FragmentCreateLoanBinding
import com.nc.finlocknc.feature.CreateLoan.model.request.AddCustomerRequest
import com.nc.finlocknc.feature.CreateLoan.viewmodel.AddCustomerViewModel
import com.nc.finlocknc.feature.CreateLoan.viewmodel.LicenseViewModel
import com.nc.finlocknc.feature.CreateLoan.viewmodel.UploadViewModel
import com.nc.finlocknc.feature.auth.PrefManager.PrefManager
import com.nc.finlocknc.feature.home.view.HomeActivity
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class CreateLoanFragment : Fragment() {

    private var _binding: FragmentCreateLoanBinding? = null
    private val binding get() = _binding!!

    private val licenseViewModel: LicenseViewModel by viewModels()
    private val uploadViewModel: UploadViewModel by viewModels()
    private val addCustomerViewModel: AddCustomerViewModel by viewModels()

    private var currentStep = 1
    private val totalSteps = 7

    // ===== STEP NAMES =====
    private val stepNames = arrayOf(
        "Customer Details",
        "Address Information",
        "Guarantor Details",
        "Loan Details",
        "Device Details",
        "Upload Documents",
        "Digital Signature"
    )

    // ===== DOCUMENT TRACKING =====
    private var deviceImage1File: File? = null
    private var deviceImage2File: File? = null
    private var userImageFile: File? = null
    private var panFile: File? = null
    private var aadharFile: File? = null
    private var consentSigned = false

    // ===== UPLOADED URLs =====
    private var uploadedDeviceImage1Url: String? = null
    private var uploadedDeviceImage2Url: String? = null
    private var uploadedUserImageUrl: String? = null
    private var uploadedPanUrl: String? = null
    private var uploadedAadharUrl: String? = null

    // ===== UPLOAD TRACKING =====
    private var totalUploads = 0
    private var completedUploads = 0
    private var uploadFailed = false
    private var isUploading = false

    // ===== BRAND & MODEL MAPPING =====
    private val brandModelsPhone = mapOf(
        "Samsung" to listOf(
            "Galaxy S25", "Galaxy Z Flip6", "Galaxy A55", "Galaxy M35",
            "Galaxy S24", "Galaxy A34", "Galaxy M14", "Galaxy F14",
            "Galaxy S23", "Galaxy A54", "Galaxy M33"
        ),
        "Google" to listOf(
            "Pixel 10", "Pixel 10 Pro", "Pixel 9a", "Pixel Fold",
            "Pixel 9", "Pixel 9 Pro", "Pixel 8a", "Pixel 8",
            "Pixel 7a", "Pixel 7 Pro"
        ),
        "Xiaomi" to listOf(
            "Redmi Note 13 Pro", "Redmi 13C", "Mi 12X", "Mi 11 Ultra",
            "Redmi Note 12", "Mi 12", "Mi 13 Ultra", "Redmi 12C",
            "Redmi Note 11", "Mi 11i"
        ),
        "OnePlus" to listOf(
            "OnePlus 12", "OnePlus 12R", "Nord CE 4", "Nord N30",
            "OnePlus 11", "Nord 3", "Nord 2T"
        ),
        "Motorola" to listOf(
            "Edge 60 Pro", "Edge 60 Neo", "Moto G75", "Moto G06",
            "Moto G73", "Moto G53", "Edge 40"
        ),
        "Oppo" to listOf(
            "Reno 12", "Reno 11 Pro", "F21 Pro", "A78",
            "Find X6", "A17", "A77"
        ),
        "Vivo" to listOf(
            "V27", "V27 Pro", "Y100", "Y77",
            "X100", "X90 Pro", "V25e"
        ),
        "Realme" to listOf(
            "GT 3", "GT 3 Master", "Narzo 60x", "Narzo 70",
            "C55", "C35", "GT Neo 6"
        ),
        "Apple" to listOf(
            "iPhone 16 Pro Max", "iPhone 16 Pro", "iPhone 16 Plus", "iPhone 16",
            "iPhone 15 Pro Max", "iPhone 15 Pro", "iPhone 15 Plus", "iPhone 15",
            "iPhone 14 Pro Max", "iPhone 14 Pro", "iPhone 14 Plus", "iPhone 14"
        ),
        "Nothing" to listOf(
            "Phone (2a)", "Phone (2)", "Phone (1)"
        )
    )

    // ✅ Tab Brands & Models
    private val brandModelsTab = mapOf(
        "Samsung" to listOf(
            "Galaxy Tab S9 Ultra", "Galaxy Tab S9", "Galaxy Tab S9 FE",
            "Galaxy Tab A9", "Galaxy Tab A8", "Galaxy Tab A7",
            "Galaxy Tab S8 Ultra", "Galaxy Tab S8", "Galaxy Tab S7"
        ),
        "Apple" to listOf(
            "iPad Pro 12.9", "iPad Pro 11", "iPad Air 5", "iPad 10th Gen",
            "iPad Mini 6", "iPad 9th Gen", "iPad Air 4"
        ),
        "Xiaomi" to listOf(
            "Mi Pad 6", "Mi Pad 6 Pro", "Mi Pad 5", "Redmi Pad SE",
            "Redmi Pad", "Mi Pad 4"
        ),
        "Lenovo" to listOf(
            "Tab P12 Pro", "Tab P11 Pro", "Tab M10 Plus", "Tab M10",
            "Tab M8", "Tab P12", "Yoga Tab 13"
        ),
        "Huawei" to listOf(
            "MatePad Pro 12.6", "MatePad Pro 11", "MatePad 11",
            "MatePad T10", "MatePad T8"
        ),
        "Realme" to listOf(
            "Pad X", "Pad Mini", "Pad 2"
        ),
        "Oppo" to listOf(
            "Pad Air", "Pad 2", "Pad"
        ),
        "Vivo" to listOf(
            "Vivo Pad 2", "Vivo Pad Air", "Vivo Pad"
        ),
        "Motorola" to listOf(
            "Tab G200", "Tab G62", "Tab G20"
        ),
        "Google" to listOf(
            "Pixel Tablet", "Pixel Slate"
        )
    )

    private var currentBrandModels = brandModelsPhone // Default to Phone

    // ===== FILE PICKER LAUNCHERS =====
    private val deviceImage1PickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    deviceImage1File = uriToFile(it)
                    showPreview(deviceImage1File!!, binding.step6Documents.tileDeviceImage1.ivDocPreview)
                    updateDocumentStatus("device1", true)
                }
            }
        }

    private val deviceImage2PickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    deviceImage2File = uriToFile(it)
                    showPreview(deviceImage2File!!, binding.step6Documents.tileDeviceImage2.ivDocPreview)
                    updateDocumentStatus("device2", true)
                }
            }
        }

    private val userImagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    userImageFile = uriToFile(it)
                    showPreview(userImageFile!!, binding.step6Documents.tileUserImage.ivDocPreview)
                    updateDocumentStatus("user", true)
                }
            }
        }

    private val panPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    panFile = uriToFile(it)
                    showPreview(panFile!!, binding.step6Documents.tilePan.ivDocPreview)
                    updateDocumentStatus("pan", true)
                }
            }
        }

    private val aadharPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    aadharFile = uriToFile(it)
                    showPreview(aadharFile!!, binding.step6Documents.tileAadhar.ivDocPreview)
                    updateDocumentStatus("aadhar", true)
                }
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

        setupObservers()
        getLicenseKey()
        setupDeviceAutoCompleteFields()
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

    // ===== SHOW/HIDE PROGRESS =====
    private fun showProgress() {
        binding.progressContainer.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progressContainer.visibility = View.GONE
    }

    // ===== SETUP DEVICE AUTOCOMPLETE FIELDS =====
    private fun setupDeviceAutoCompleteFields() {
        // Set default Phone brand list
        val brandList = brandModelsPhone.keys.toList()
        val brandAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            brandList
        )
        binding.step5Device.autoCompleteBrand.setAdapter(brandAdapter)

        binding.step5Device.autoCompleteBrand.setOnClickListener {
            binding.step5Device.autoCompleteBrand.showDropDown()
        }
        binding.step5Device.autoCompleteBrand.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.step5Device.autoCompleteBrand.showDropDown()
        }

        binding.step5Device.autoCompleteBrand.setOnItemClickListener { _, _, position, _ ->
            val selectedBrand = brandList[position]
            val models = currentBrandModels[selectedBrand] ?: emptyList()
            val modelAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                models
            )
            binding.step5Device.autoCompleteModel.setAdapter(modelAdapter)
            binding.step5Device.autoCompleteModel.text?.clear()
        }

        binding.step5Device.autoCompleteModel.setOnClickListener {
            binding.step5Device.autoCompleteModel.showDropDown()
        }
        binding.step5Device.autoCompleteModel.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.step5Device.autoCompleteModel.showDropDown()
        }

        // ✅ Device type Radio Button selection
        binding.step5Device.dtPhone.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentBrandModels = brandModelsPhone
                updateBrandList(currentBrandModels)
                Toast.makeText(requireContext(), "Phone mode selected", Toast.LENGTH_SHORT).show()
            }
        }

        binding.step5Device.dtTab.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentBrandModels = brandModelsTab
                updateBrandList(currentBrandModels)
                Toast.makeText(requireContext(), "Tab mode selected", Toast.LENGTH_SHORT).show()
            }
        }

        // IMEI scan click listeners
        binding.step5Device.etImei1.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val drawableEnd = binding.step5Device.etImei1.compoundDrawablesRelative[2]
                if (drawableEnd != null) {
                    val drawableBounds = drawableEnd.bounds
                    val tapX = event.x
                    val xLimit = binding.step5Device.etImei1.width - binding.step5Device.etImei1.paddingEnd
                    if (tapX >= (xLimit - drawableBounds.width())) {
                        Toast.makeText(requireContext(), "Scan IMEI 1", Toast.LENGTH_SHORT).show()
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }

        binding.step5Device.etImei2.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val drawableEnd = binding.step5Device.etImei2.compoundDrawablesRelative[2]
                if (drawableEnd != null) {
                    val drawableBounds = drawableEnd.bounds
                    val tapX = event.x
                    val xLimit = binding.step5Device.etImei2.width - binding.step5Device.etImei2.paddingEnd
                    if (tapX >= (xLimit - drawableBounds.width())) {
                        Toast.makeText(requireContext(), "Scan IMEI 2", Toast.LENGTH_SHORT).show()
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }

    // ✅ Helper function to update brand list
    private fun updateBrandList(brandModels: Map<String, List<String>>) {
        val newBrandList = brandModels.keys.toList()
        val brandAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            newBrandList
        )
        binding.step5Device.autoCompleteBrand.setAdapter(brandAdapter)
        binding.step5Device.autoCompleteBrand.text?.clear()
        binding.step5Device.autoCompleteModel.text?.clear()
        binding.step5Device.autoCompleteModel.setAdapter(null)
    }

    // ===== SETUP OBSERVERS =====
    private fun setupObservers() {

        uploadViewModel.uploadResponse.observe(viewLifecycleOwner) { result ->

            result.onSuccess { uploadResult ->

                val uploadedUrl = uploadResult.response.url

                when (uploadResult.type) {
                    "device1" -> uploadedDeviceImage1Url = uploadedUrl
                    "device2" -> uploadedDeviceImage2Url = uploadedUrl
                    "user" -> uploadedUserImageUrl = uploadedUrl
                    "pan" -> uploadedPanUrl = uploadedUrl
                    "aadhar" -> uploadedAadharUrl = uploadedUrl
                }

                completedUploads++
                checkUploadsComplete()

            }.onFailure { error ->

                hideProgress()

                binding.btnNext.isEnabled = true
                binding.btnNext.text = "Submit Application"

                Toast.makeText(
                    requireContext(),
                    error.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Customer API Response
        addCustomerViewModel.customerPostData.observe(viewLifecycleOwner) { response ->

            hideProgress()

            binding.btnNext.isEnabled = true
            binding.btnNext.text = "Submit Application"

            if (response.status.equals("success", true)) {

                Toast.makeText(
                    requireContext(),
                    response.message,
                    Toast.LENGTH_LONG
                ).show()

                requireActivity()
                    .supportFragmentManager
                    .popBackStack()

            } else {

                Toast.makeText(
                    requireContext(),
                    response.message,
                    Toast.LENGTH_LONG
                ).show()

                if (
                    response.message.contains(
                        "IMEI already exists",
                        true
                    )
                ) {

                    currentStep = 5

                    showStep(currentStep)

                    updateStepIndicator(currentStep)

                    binding.step5Device.etImei1.error =
                        "IMEI already exists"

                    binding.step5Device.etImei1.requestFocus()
                }
            }
        }

        addCustomerViewModel.error.observe(viewLifecycleOwner) {

            hideProgress()

            binding.btnNext.isEnabled = true
            binding.btnNext.text = "Submit Application"

            Toast.makeText(
                requireContext(),
                it,
                Toast.LENGTH_LONG
            ).show()
        }

        addCustomerViewModel.isLoading.observe(viewLifecycleOwner) {

            if (it) {
                showProgress()
            } else {
                hideProgress()
            }
        }
    }    // ===== LICENSE KEY =====
    private fun getLicenseKey() {
        val prefManager = PrefManager(requireContext())
        val retailerId = prefManager.getRetailerId()

        if (retailerId <= 0) {
            Toast.makeText(requireContext(), "Retailer ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        licenseViewModel.getLicenseKey(retailerId)

        licenseViewModel.licenseKey.observe(viewLifecycleOwner) { response ->
            if (response.isSuccess && !response.license_key.isNullOrEmpty()) {
                prefManager.saveLicenseKey(response.license_key)
            } else {
                Toast.makeText(requireContext(), "License key not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ===== URI TO FILE CONVERTER =====
    private fun uriToFile(uri: Uri): File? {
        return try {
            val contentResolver = requireContext().contentResolver
            val mimeType = contentResolver.getType(uri) ?: return null
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"

            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("upload_", ".$extension", requireContext().cacheDir)

            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ===== SHOW PREVIEW =====
    private fun showPreview(file: File, imageView: android.widget.ImageView) {
        val extension = file.extension.lowercase()
        if (extension == "pdf") {
            imageView.setImageResource(R.drawable.ic_briefcase)
        } else {
            Glide.with(requireContext())
                .load(file)
                .placeholder(R.drawable.user_svg)
                .error(R.drawable.user_svg)
                .into(imageView)
        }
    }

    // ===== DOCUMENT UPLOADS =====
    private fun setupDocumentTitles() {
        binding.step6Documents.tileUserImage.tvDocTitle.text = "User Photo"
        binding.step6Documents.tileDeviceImage1.tvDocTitle.text = "Device Image (Front)"
        binding.step6Documents.tileDeviceImage2.tvDocTitle.text = "Device Image (Back)"
        binding.step6Documents.tilePan.tvDocTitle.text = "PAN Card"
        binding.step6Documents.tileAadhar.tvDocTitle.text = "Aadhaar Card"
    }

    private fun setupDocumentUploads() {
        binding.step6Documents.tileDeviceImage1.root.setOnClickListener {
            openFilePicker(deviceImage1PickerLauncher, "Select Device Image (Front)")
        }
        binding.step6Documents.tileDeviceImage2.root.setOnClickListener {
            openFilePicker(deviceImage2PickerLauncher, "Select Device Image (Back)")
        }
        binding.step6Documents.tileUserImage.root.setOnClickListener {
            openFilePicker(userImagePickerLauncher, "Select User Photo")
        }
        binding.step6Documents.tilePan.root.setOnClickListener {
            openFilePicker(panPickerLauncher, "Select PAN Card")
        }
        binding.step6Documents.tileAadhar.root.setOnClickListener {
            openFilePicker(aadharPickerLauncher, "Select Aadhaar Card")
        }
    }

    private fun openFilePicker(launcher: androidx.activity.result.ActivityResultLauncher<Intent>, title: String) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/* application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        launcher.launch(Intent.createChooser(intent, title))
    }

    private fun setupConsentListeners() {
        binding.step6Documents.btnViewConsent.setOnClickListener {
            Toast.makeText(requireContext(), "Opening Consent Document...", Toast.LENGTH_SHORT).show()
        }

        binding.step6Documents.btnSignConsent.setOnClickListener {
            consentSigned = true
            binding.step6Documents.tvConsentStatus.text = "✓ Signed"
            binding.step6Documents.tvConsentStatus.setTextColor(resources.getColor(R.color.green))
            binding.step6Documents.tvConsentStatus.setBackgroundResource(R.drawable.bg_done_badge)
            updateOptionalDocsStatus()
            Toast.makeText(requireContext(), "Consent Signed Successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateDocumentStatus(docType: String, isUploaded: Boolean) {
        val tile = when (docType) {
            "device1" -> binding.step6Documents.tileDeviceImage1
            "device2" -> binding.step6Documents.tileDeviceImage2
            "user" -> binding.step6Documents.tileUserImage
            "pan" -> binding.step6Documents.tilePan
            "aadhar" -> binding.step6Documents.tileAadhar
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
            deviceImage1File,
            deviceImage2File,
            userImageFile
        ).all { it != null }

        binding.step6Documents.tvMandatoryDone.visibility = if (allUploaded) View.VISIBLE else View.GONE
    }

    private fun updateOptionalDocsStatus() {
        val allUploaded = listOf(
            panFile,
            aadharFile
        ).all { it != null } && consentSigned

        binding.step6Documents.tvOptionalDone.visibility = if (allUploaded) View.VISIBLE else View.GONE
    }

    private fun updateDocsCounter() {
        val mandatoryCount = listOf(
            deviceImage1File,
            deviceImage2File,
            userImageFile
        ).count { it != null }

        val optionalCount = listOf(
            panFile,
            aadharFile
        ).count { it != null } + if (consentSigned) 1 else 0

        val totalUploaded = mandatoryCount + optionalCount
        binding.step6Documents.tvDocsCounter.text = "$totalUploaded of 7 completed"
    }

    // ===== UPLOAD LOGIC =====
    private fun uploadDocuments() {
        val prefManager = PrefManager(requireContext())
        val licenseKey = prefManager.getLicenseKey()

        if (licenseKey.isEmpty()) {
            Toast.makeText(requireContext(), "License key not found", Toast.LENGTH_SHORT).show()
            return
        }

        totalUploads = 0
        completedUploads = 0
        uploadFailed = false
        isUploading = true

        // Show progress
        showProgress()

        uploadedDeviceImage1Url = null
        uploadedDeviceImage2Url = null
        uploadedUserImageUrl = null
        uploadedPanUrl = null
        uploadedAadharUrl = null

        if (deviceImage1File != null) totalUploads++
        if (deviceImage2File != null) totalUploads++
        if (userImageFile != null) totalUploads++
        if (panFile != null) totalUploads++
        if (aadharFile != null) totalUploads++

        deviceImage1File?.let { uploadViewModel.uploadImage(licenseKey, "device1", it) }
        deviceImage2File?.let { uploadViewModel.uploadImage(licenseKey, "device2", it) }
        userImageFile?.let { uploadViewModel.uploadImage(licenseKey, "user", it) }
        panFile?.let { uploadViewModel.uploadImage(licenseKey, "pan", it) }
        aadharFile?.let { uploadViewModel.uploadImage(licenseKey, "aadhar", it) }

        if (totalUploads == 0) {
            isUploading = false
            hideProgress()
            submitLoanApplication()
        }
    }

    private fun checkUploadsComplete() {
        if (completedUploads == totalUploads) {

            // Keep loader visible
            submitLoanApplication()
        }

        if (completedUploads == totalUploads) {
            isUploading = false
            hideProgress()
            submitLoanApplication()
        }
    }

    // ===== STEP NAVIGATION =====
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
                    if (!isUploading) {
                        binding.btnNext.isEnabled = false
                        binding.btnNext.text = "Uploading..."
                        uploadDocuments()
                    }
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
        val dotStep7 = view?.findViewById<View>(R.id.dot_step7)

        val tvStepLabel = view?.findViewById<TextView>(R.id.tv_step_label)
        val tvStepName = view?.findViewById<TextView>(R.id.tv_step_name)

        tvStepLabel?.text = "Step $step of $totalSteps"
        tvStepName?.text = stepNames[step - 1]

        val dots = listOf(dotStep1, dotStep2, dotStep3, dotStep4, dotStep5, dotStep6, dotStep7)
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
            7 -> dotStep7?.setBackgroundResource(R.drawable.bg_step_dot_active)
        }

        for (i in 1 until step) {
            when (i) {
                1 -> dotStep1?.setBackgroundResource(R.drawable.bg_step_dot_completed)
                2 -> dotStep2?.setBackgroundResource(R.drawable.bg_step_dot_completed)
                3 -> dotStep3?.setBackgroundResource(R.drawable.bg_step_dot_completed)
                4 -> dotStep4?.setBackgroundResource(R.drawable.bg_step_dot_completed)
                5 -> dotStep5?.setBackgroundResource(R.drawable.bg_step_dot_completed)
                6 -> dotStep6?.setBackgroundResource(R.drawable.bg_step_dot_completed)
                7 -> dotStep7?.setBackgroundResource(R.drawable.bg_step_dot_completed)
            }
        }
    }

    private fun showStep(step: Int) {
        val steps = listOf(
            binding.step1Customer.root,
            binding.step2Address.root,
            binding.step3Guarantor.root,
            binding.step4Loan.root,
            binding.step5Device.root,
            binding.step6Documents.root,
            binding.step7Signature.root
        )

        steps.forEach { it.visibility = View.GONE }
        steps[step - 1].visibility = View.VISIBLE

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
            5 -> binding.step5Device.tvDeviceDone.visibility = View.VISIBLE
            6 -> binding.step6Documents.tvDocumentsDone.visibility = View.VISIBLE
            7 -> binding.step7Signature.tvSigDone.visibility = View.VISIBLE
        }
    }

    // ===== VALIDATION =====
    private fun validateCurrentStep(): Boolean {
        return when (currentStep) {
            1 -> validateStep1()
            2 -> validateStep2()
            3 -> validateStep3()
            4 -> validateStep4()
            5 -> validateStep5()
            6 -> validateStep6()
            7 -> validateStep7()
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
        var isValid = true

        if (!binding.step5Device.dtPhone.isChecked && !binding.step5Device.dtTab.isChecked) {
            Toast.makeText(requireContext(), "Please select device type", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        val brand = binding.step5Device.autoCompleteBrand.text.toString().trim()
        if (brand.isEmpty()) {
            binding.step5Device.autoCompleteBrand.error = "Please select brand"
            isValid = false
        } else {
            binding.step5Device.autoCompleteBrand.error = null
        }

        val model = binding.step5Device.autoCompleteModel.text.toString().trim()
        if (model.isEmpty()) {
            binding.step5Device.autoCompleteModel.error = "Please select model"
            isValid = false
        } else {
            binding.step5Device.autoCompleteModel.error = null
        }

        val imei1 = binding.step5Device.etImei1.text.toString().trim()
        if (imei1.isEmpty()) {
            binding.step5Device.etImei1.error = "IMEI 1 is required"
            isValid = false
        } else if (imei1.length != 15 || !imei1.all { it.isDigit() }) {
            binding.step5Device.etImei1.error = "Enter valid 15-digit IMEI number"
            isValid = false
        } else {
            binding.step5Device.etImei1.error = null
        }

        val imei2 = binding.step5Device.etImei2.text.toString().trim()
        if (imei2.isNotEmpty()) {
            if (imei2.length != 15 || !imei2.all { it.isDigit() }) {
                binding.step5Device.etImei2.error = "Enter valid 15-digit IMEI number"
                isValid = false
            } else if (imei1 == imei2) {
                binding.step5Device.etImei2.error = "IMEI 1 and IMEI 2 cannot be the same"
                isValid = false
            } else {
                binding.step5Device.etImei2.error = null
            }
        }

        if (isValid) {
            Toast.makeText(requireContext(), "✓ Device details saved", Toast.LENGTH_SHORT).show()
        }
        return isValid
    }

    private fun validateStep6(): Boolean {
        if (deviceImage1File == null) {
            Toast.makeText(requireContext(), "Please upload Device Image (Front)", Toast.LENGTH_SHORT).show()
            return false
        }

        if (deviceImage2File == null) {
            Toast.makeText(requireContext(), "Please upload Device Image (Back)", Toast.LENGTH_SHORT).show()
            return false
        }

        if (userImageFile == null) {
            Toast.makeText(requireContext(), "Please upload User Photo", Toast.LENGTH_SHORT).show()
            return false
        }

        if (panFile == null) {
            Toast.makeText(requireContext(), "⚠️ PAN Card is optional but recommended", Toast.LENGTH_SHORT).show()
        }

        if (aadharFile == null) {
            Toast.makeText(requireContext(), "⚠️ Aadhaar Card is optional but recommended", Toast.LENGTH_SHORT).show()
        }

        if (!consentSigned) {
            Toast.makeText(requireContext(), "⚠️ Consent signing is optional but recommended", Toast.LENGTH_SHORT).show()
        }

        return true
    }

    private fun validateStep7(): Boolean {
        if (!binding.step7Signature.signatureView.hasSignature()) {
            Toast.makeText(requireContext(), "Please draw and save your signature", Toast.LENGTH_SHORT).show()
            return false
        }
        Toast.makeText(requireContext(), "✓ Signature saved", Toast.LENGTH_SHORT).show()
        return true
    }

    // ===== DATE PICKER =====
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

    // ===== SIGNATURE =====
    private fun setupSignature() {
        binding.step7Signature.signatureView.setOnTouchListener { _, _ ->
            binding.step7Signature.tvSignatureHint.visibility = View.GONE
            false
        }

        binding.step7Signature.btnClearSignature.setOnClickListener {
            binding.step7Signature.signatureView.clear()
            binding.step7Signature.tvSignatureHint.visibility = View.VISIBLE
            binding.step7Signature.tvSignatureStatus.text = "Signature required to proceed"
            binding.step7Signature.tvSigDone.visibility = View.GONE
        }

        binding.step7Signature.btnSaveSignature.setOnClickListener {
            if (binding.step7Signature.signatureView.hasSignature()) {
                saveSignature()
                binding.step7Signature.tvSignatureHint.visibility = View.GONE
                binding.step7Signature.tvSignatureStatus.text = "✓ Signature saved"
                binding.step7Signature.tvSignatureStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                binding.step7Signature.tvSigDone.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Signature saved successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please draw your signature first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveSignature() {
        val signatureBitmap = binding.step7Signature.signatureView.getSignatureBitmap()
        signatureBitmap?.let {
            val file = File(requireContext().cacheDir, "signature_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { outputStream ->
                it.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
        }
    }

    // ===== LOAN CALCULATION =====
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

    // ===== SUBMIT LOAN APPLICATION =====
    private fun submitLoanApplication() {
        val prefManager = PrefManager(requireContext())
        val savedLicenseKey = prefManager.getLicenseKey()
        val retailerKey = prefManager.getRetailerId()

        if (savedLicenseKey.isEmpty()) {
            Toast.makeText(requireContext(), "License key not found. Please try again.", Toast.LENGTH_SHORT).show()
            binding.btnNext.isEnabled = true
            binding.btnNext.text = "Submit Application"
            return
        }

        // Collect all data
        val name = binding.step1Customer.etCustomerName.text.toString().trim()
        val email = binding.step1Customer.etEmail.text.toString().trim()
        val contact = binding.step1Customer.etMobile.text.toString().trim()
        val guarantorName = binding.step3Guarantor.etGuarantorName.text.toString().trim()
        val guarantorMobile = binding.step3Guarantor.etGuarantorMobile.text.toString().trim()
        val productAmount = binding.step4Loan.etLoanAmount.text.toString().trim()
        val noOfEmi = binding.step4Loan.etTenure.text.toString().trim().toIntOrNull() ?: 0
        val downPayment = "0"
        val balanceAmount = productAmount
        val emiAmount = binding.step4Loan.tvEmiAmount.text.toString().replace("₹ ", "").trim()
        val interestRate = binding.step4Loan.etInterestRate.text.toString().trim()
        val netAmount = productAmount
        val description = binding.step1Customer.etOccupation.text.toString().trim()

        // Device details from Step 5
        val deviceType = if (binding.step5Device.dtPhone.isChecked) "Phone" else "Tab"
        val mobileBrand = binding.step5Device.autoCompleteBrand.text.toString().trim()
        val mobileModel = binding.step5Device.autoCompleteModel.text.toString().trim()
        val imei1 = binding.step5Device.etImei1.text.toString().trim()
        val imei2 = binding.step5Device.etImei2.text.toString().trim()

        val devicename = "FinLock_${System.currentTimeMillis()}"
        val deviceId = "DEV_${System.currentTimeMillis()}"
        val status = "open"
        val mobileStatus = 1

        val request = AddCustomerRequest(
            name = name,
            email = email,
            contact = contact,
            //pan = "",
          //  aadhar = "",
            guaranator_name = guarantorName,
            guaranator_mobile = guarantorMobile,
            product_amount = productAmount,
            no_of_emi = noOfEmi,
            //financer_bank = "",
            down_payment = downPayment,
            balance_amount = balanceAmount,
            emi_amount = emiAmount,
            interest_rate = interestRate,
            net_amount = netAmount,
            description = description,
            mobile_brand = mobileBrand,
            mobile_model = mobileModel,
            device_type = deviceType,
            devicename = devicename,
            imei = imei1,
            imeiII = imei2,
            device_id = deviceId,
            license_key = savedLicenseKey,
            status = status,
            mobile_status = mobileStatus,
            pan_file = uploadedPanUrl ?: "",
            customer_profile_pic = uploadedUserImageUrl ?: "",
            aadhar_file = uploadedAadharUrl ?: "",
            consent_file = "",
            customer_sign = "",
            retailer_key = retailerKey
        )

        // Show progress while submitting
        showProgress()
        addCustomerViewModel.postCustomer(request)
        Toast.makeText(requireContext(), "Submitting loan application...", Toast.LENGTH_SHORT).show()
    }

    // ===== TENURE DROPDOWN =====
    private fun setupTenureDropdown() {
        val tenureList = listOf(
            "3 Months", "6 Months", "9 Months", "12 Months",
            "18 Months", "24 Months", "36 Months", "48 Months", "60 Months"
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

    // ===== EMI CALCULATOR =====
    private fun calculateEMI(principal: Double, rate: Double, tenureInMonths: Int): Double {
        val monthlyRate = rate / (12 * 100)
        val emi = principal * monthlyRate * Math.pow(1 + monthlyRate, tenureInMonths.toDouble()) /
                (Math.pow(1 + monthlyRate, tenureInMonths.toDouble()) - 1)
        return if (emi.isNaN()) 0.0 else emi
    }

    // ===== LIFECYCLE =====
    override fun onResume() {
        super.onResume()
        (activity as? HomeActivity)?.hideMainUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? HomeActivity)?.showMainUi()
        hideProgress()
        _binding = null
    }
}