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
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.e_patrakaar.databinding.ActivityOtpBinding
import com.example.e_patrakaar.utils.Constants
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

    lateinit var binding : ActivityOtpBinding 
    private lateinit var OTP :String
    private lateinit var verificationBtn :Button
    private lateinit var resendBtn : Button
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

        //  Fetching data from fragment to activity
//        OTP = intent.getStringExtra("OTP").toString()
//        resendingToken = intent.getParcelableExtra("resendToken")!!
//        phoneNumber = intent.getStringExtra("phonNumber")!!

        init()
        addTextChangeListener()
        resendBtn.setOnClickListener {
            resendVerificationCode()
        }

        verificationBtn.setOnClickListener {
            val typeOTP =(inpitOTP1.text.toString()+inpitOTP2.text.toString()+inpitOTP3.text.toString()+inpitOTP4.text.toString()+
                    inpitOTP5.text.toString()+inpitOTP6.text.toString())

            if (typeOTP.isNotEmpty()){
                if (typeOTP.length==6){
                    val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        OTP, typeOTP
                    )
                    signInWithPhoneAuthCredential(credential)
                }else{

                    Toast.makeText(this,"Please Enter Correct OTP", Toast.LENGTH_SHORT).show()

                }

            }else{
                Toast.makeText(this,"Please Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun resendVerificationCode(){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendingToken)// OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
   private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            OTP = verificationId
            resendingToken = token
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(Constants.TAG, "signInWithCredential:success")
                    val user = task.result?.user
                    Toast.makeText(
                        this,
                        "Authenticate Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    sendBackToEditProfile()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(Constants.TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }
    private fun sendBackToEditProfile(){
        // Now you have to write the code for going back to EditProfile

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