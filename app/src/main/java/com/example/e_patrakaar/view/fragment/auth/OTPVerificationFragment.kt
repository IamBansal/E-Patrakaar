package com.example.e_patrakaar.view.fragment.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.e_patrakaar.databinding.FragmentOTPVerificationBinding
import com.example.e_patrakaar.view.activity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider

class OTPVerificationFragment : Fragment() {

    private lateinit var binding: FragmentOTPVerificationBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOTPVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.tvPhone.text = "Please enter the verification code sent to +91 ${arguments?.getString("number")}"

        binding.btnSubmit.setOnClickListener {
            verifyOTP()
        }

    }

    private fun verifyOTP() {
        val userOTP = binding.OTPPinView.text.toString().trim()
        val backendOTP = arguments?.getString("otp")

        if (TextUtils.isEmpty(userOTP)) {
            Toast.makeText(requireActivity(), "Please enter OTP first.", Toast.LENGTH_SHORT).show()
        } else {
            val phoneAuthCredential = backendOTP?.let { PhoneAuthProvider.getCredential(it, userOTP) }
            if (phoneAuthCredential != null) {
                auth.signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("check", "FirebaseAuth credentials verified")

//                            val user = auth.currentUser
//                            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
//                            user!!.updateProfile(profileUpdates)
//                            incorrectOTP.visibility = View.GONE

                            startActivity(Intent(requireActivity(), MainActivity::class.java))
                            requireActivity().finish()

                        } else {
                            Log.d("check", "Enter correct OTP")
                            Toast.makeText(requireActivity(), "Enter correct OTP", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

    }

}