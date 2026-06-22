package com.nc.finlocknc.feature.set_new_mpin.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nc.finlocknc.R
import com.nc.finlocknc.databinding.FragmentSetNewMpinBinding
import com.nc.finlocknc.feature.auth.PrefManager.PrefManager
import com.nc.finlocknc.feature.home.view.HomeActivity

class SetNewMpinFragment : Fragment() {

    private var _binding: FragmentSetNewMpinBinding? = null
    private val binding get() = _binding!!

    private lateinit var newMpinBoxes: Array<android.widget.EditText>
    private lateinit var confirmMpinBoxes: Array<android.widget.EditText>
    private var isNewMpinSet = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetNewMpinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeDigitBoxes()
        setupAutoMove()
        setupDeleteHandler()

        // Setup toolbar back button
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnSetNewMpin.setOnClickListener {
            val newMpin = getMpinFromBoxes(newMpinBoxes)
            val confirmMpin = getMpinFromBoxes(confirmMpinBoxes)

            when {
                newMpin.length != 4 -> {
                    Toast.makeText(requireContext(), "Enter complete 4-digit MPIN", Toast.LENGTH_SHORT).show()
                }
                confirmMpin.length != 4 -> {
                    Toast.makeText(requireContext(), "Enter complete 4-digit confirm MPIN", Toast.LENGTH_SHORT).show()
                }
                newMpin != confirmMpin -> {
                    Toast.makeText(requireContext(), "MPIN does not match", Toast.LENGTH_SHORT).show()
                    clearAllBoxes()
                }
                else -> {
                    // Save new MPIN
                    PrefManager(requireContext()).saveMPin(newMpin)
                    Toast.makeText(requireContext(), "New MPIN set successfully", Toast.LENGTH_SHORT).show()

                    // Navigate back to Settings page
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun initializeDigitBoxes() {
        // New MPIN boxes
        newMpinBoxes = arrayOf(
            binding.newDigit1, binding.newDigit2, binding.newDigit3,
            binding.newDigit4
        )

        // Confirm MPIN boxes
        confirmMpinBoxes = arrayOf(
            binding.confirmDigit1, binding.confirmDigit2, binding.confirmDigit3,
            binding.confirmDigit4
        )
    }

    private fun setupAutoMove() {
        // Setup auto-move for New MPIN boxes
        for (i in newMpinBoxes.indices) {
            newMpinBoxes[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && i < newMpinBoxes.size - 1) {
                        newMpinBoxes[i + 1].requestFocus()
                    }
                    // Auto move to confirm pin first box when all new pin digits are filled
                    if (i == newMpinBoxes.size - 1 && s?.length == 1) {
                        confirmMpinBoxes[0].requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }

        // Setup auto-move for Confirm MPIN boxes
        for (i in confirmMpinBoxes.indices) {
            confirmMpinBoxes[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && i < confirmMpinBoxes.size - 1) {
                        confirmMpinBoxes[i + 1].requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun setupDeleteHandler() {
        // Setup delete handler for New MPIN boxes
        for (i in newMpinBoxes.indices) {
            newMpinBoxes[i].setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (newMpinBoxes[i].text.isEmpty() && i > 0) {
                        newMpinBoxes[i - 1].requestFocus()
                        newMpinBoxes[i - 1].setText("")
                    }
                }
                false
            }
        }

        // Setup delete handler for Confirm MPIN boxes
        for (i in confirmMpinBoxes.indices) {
            confirmMpinBoxes[i].setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (confirmMpinBoxes[i].text.isEmpty() && i > 0) {
                        confirmMpinBoxes[i - 1].requestFocus()
                        confirmMpinBoxes[i - 1].setText("")
                    } else if (i == 0 && confirmMpinBoxes[i].text.isEmpty()) {
                        // If first confirm box is empty, move to last new pin box
                        newMpinBoxes[newMpinBoxes.size - 1].requestFocus()
                        newMpinBoxes[newMpinBoxes.size - 1].setText("")
                    }
                }
                false
            }
        }
    }

    private fun getMpinFromBoxes(boxes: Array<android.widget.EditText>): String {
        val mpin = StringBuilder()
        for (box in boxes) {
            mpin.append(box.text.toString())
        }
        return mpin.toString()
    }

    private fun clearAllBoxes() {
        // Clear New MPIN boxes
        for (box in newMpinBoxes) {
            box.setText("")
        }
        // Clear Confirm MPIN boxes
        for (box in confirmMpinBoxes) {
            box.setText("")
        }
        // Focus on first new pin box
        newMpinBoxes[0].requestFocus()
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