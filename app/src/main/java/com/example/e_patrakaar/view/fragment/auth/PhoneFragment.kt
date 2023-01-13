package com.example.e_patrakaar.view.fragment.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.e_patrakaar.R
import com.example.e_patrakaar.databinding.FragmentPhoneBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneFragment : Fragment() {

    private lateinit var binding: FragmentPhoneBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRequest.setOnClickListener {
            sendOTP()

        }
        auth = FirebaseAuth.getInstance()
    }

    private fun sendOTP() {
        val phoneNumber = binding.etPhone.text.toString().trim()
        if (phoneNumber.isNotEmpty()) {
            if (phoneNumber.length == 10) {

                val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                            Log.d(tag, "onVerificationCompleted:$credential")
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            Log.w(tag, "onVerificationFailed", e)
                            Toast.makeText(requireActivity(), e.message, Toast.LENGTH_SHORT).show()
                        }

                        override fun onCodeSent(
                            verificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            Log.d(tag, "onCodeSent:$verificationId")

                            val bundle = bundleOf(("otp" to verificationId), ("number" to phoneNumber))
                            findNavController().navigate(R.id.action_navigation_phone_to_navigation_otp, bundle)

//                            val intent = Intent(req, OTPVerification::class.java)
//                            intent.putExtra("otp", verificationId)
//                            intent.putExtra("username", nameText)
//                            startActivity(intent)
                        }
                    }

                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber("+91$phoneNumber")
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(requireActivity())
                    .setCallbacks(callbacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)

            } else {
                Toast.makeText(requireActivity(), "Please enter correct mobile number", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireActivity(), "PLease enter phone number first.", Toast.LENGTH_SHORT).show()
        }
    }

}