package com.example.e_patrakaar.view.fragment.auth

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.e_patrakaar.R
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.tvPhone.text = getString(R.string.phone_number,
            arguments?.getString("code").toString(), arguments?.getString("number").toString())
        countdown()

        //For changing the number
        binding.tvPhone.setOnTouchListener(
            OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.x >= binding.tvPhone.width - binding.tvPhone.totalPaddingEnd) {
                    val dialog = AlertDialog.Builder(requireActivity())
                    dialog.setMessage("You will be re-directed to the previous page.\nYou sure you want to edit the number?")
                        .setTitle("Edit phone number?")
                        .setPositiveButton("Yes") { _, _ ->
                            findNavController().navigateUp()
                        }
                        .setNegativeButton("No") { _, _ -> }
                        .create()
                        .show()
                    return@OnTouchListener true
                }
            }
            return@OnTouchListener true
        })

        binding.btnSubmit.setOnClickListener {
            verifyOTP()
        }

    }

    private fun countdown() {
        object : CountDownTimer(31000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                binding.timer.text = " 00:${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                Toast.makeText(requireActivity(), "Time's up, Request OTP again.", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }.start()
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