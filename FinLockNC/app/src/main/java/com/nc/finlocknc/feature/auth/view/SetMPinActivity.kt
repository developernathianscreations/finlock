package com.nc.finlocknc.feature.auth.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nc.finlocknc.R
import com.nc.finlocknc.databinding.ActivitySetMpinBinding
import com.nc.finlocknc.feature.auth.PrefManager.PrefManager

class SetMPinActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetMpinBinding
    private lateinit var digitBoxes: Array<EditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySetMpinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeDigitBoxes()
        setupAutoMove()
        setupDeleteHandler()

        binding.btnSetMPin.setOnClickListener {
            val mpin = getMpinFromBoxes()
            val confirmMpin = "" // For confirm, you can add another set of boxes if needed

            if (mpin.length != 4) {
                Toast.makeText(this, "Enter complete 4-digit MPIN", Toast.LENGTH_SHORT).show()
            } else {
                PrefManager(this).saveMPin(mpin)
                Toast.makeText(this, "MPIN set successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun initializeDigitBoxes() {
        digitBoxes = arrayOf(
            binding.digit1, binding.digit2, binding.digit3,
            binding.digit4
        )
    }

    private fun setupAutoMove() {
        for (i in digitBoxes.indices) {
            digitBoxes[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && i < digitBoxes.size - 1) {
                        digitBoxes[i + 1].requestFocus()
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

    private fun getMpinFromBoxes(): String {
        val mpin = StringBuilder()
        for (box in digitBoxes) {
            mpin.append(box.text.toString())
        }
        return mpin.toString()
    }
}