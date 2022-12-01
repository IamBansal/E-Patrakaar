package com.example.e_patrakaar


import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Message
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide.init
import com.example.e_patrakaar.databinding.ActivityMainBinding
import com.example.e_patrakaar.databinding.ActivityOtpBinding
import com.example.e_patrakaar.view.fragment.ui.EditProfileFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {


    private lateinit var OTP :String
    private lateinit var resendingToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber: String
    private lateinit var inpitOTP1 : EditText
    private lateinit var inpitOTP2 : EditText
    private lateinit var inpitOTP3 : EditText
    private lateinit var inpitOTP4 : EditText
    private lateinit var inpitOTP5 : EditText
    private lateinit var inpitOTP6 : EditText

    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        OTP = intent.getStringExtra("OTP").toString()
        resendingToken = intent.getParcelableExtra("resendToken")!!
        phoneNumber = intent.getStringExtra("phonNumber")!!

        init()
    }
    private fun addTextChangeListener(){
        inpitOTP1.addTextChangedListener(EditTextWatcher(inpitOTP1))
        inpitOTP2.addTextChangedListener(EditTextWatcher(inpitOTP2))
        inpitOTP3.addTextChangedListener(EditTextWatcher(inpitOTP3))
        inpitOTP4.addTextChangedListener(EditTextWatcher(inpitOTP4))
        inpitOTP5.addTextChangedListener(EditTextWatcher(inpitOTP5))
        inpitOTP6.addTextChangedListener(EditTextWatcher(inpitOTP6))

    }
    private fun init() {
        auth = FirebaseAuth.getInstance()
        inpitOTP1 = findViewById(R.id.otpEditText1)
        inpitOTP2 = findViewById(R.id.otpEditText2)
        inpitOTP3 = findViewById(R.id.otpEditText3)
        inpitOTP4 = findViewById(R.id.otpEditText4)
        inpitOTP5 = findViewById(R.id.otpEditText5)
        inpitOTP6 = findViewById(R.id.otpEditText6)

    }
    inner class EditTextWatcher(private val  view: View) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            TODO("Not yet implemented")
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            TODO("Not yet implemented")
        }

        override fun afterTextChanged(s: Editable?) {

            val text = s.toString()
            when(view.id){
                R.id.otpEditText1 -> if (text.length == 1) inpitOTP2.requestFocus()
                R.id.otpEditText2 -> if (text.length == 1) inpitOTP3.requestFocus() else if (text.isEmpty()) inpitOTP1.requestFocus()
                R.id.otpEditText3 -> if (text.length == 1) inpitOTP4.requestFocus()else if (text.isEmpty()) inpitOTP2.requestFocus()
                R.id.otpEditText4 -> if (text.length == 1) inpitOTP5.requestFocus()else if (text.isEmpty()) inpitOTP3.requestFocus()
                R.id.otpEditText5 -> if (text.length == 1) inpitOTP6.requestFocus()else if (text.isEmpty()) inpitOTP4.requestFocus()
                R.id.otpEditText6 -> if (text.isEmpty()) inpitOTP5.requestFocus()

            }
        }
    }
}