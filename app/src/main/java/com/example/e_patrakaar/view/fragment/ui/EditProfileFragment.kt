package com.example.e_patrakaar.view.fragment.ui
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.e_patrakaar.OtpActivity
import com.example.e_patrakaar.R
import com.example.e_patrakaar.databinding.CustomBottomSheet2Binding
import com.example.e_patrakaar.databinding.FragmentEditProfileBinding
import com.example.e_patrakaar.utils.Constants.TAG
import com.example.e_patrakaar.view.activity.MainActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.time.Year
import java.util.Calendar
import java.util.concurrent.TimeUnit

class EditProfileFragment : Fragment() {

    val storage by lazy {
        FirebaseStorage.getInstance()
    }
    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    val database by lazy {
        FirebaseFirestore.getInstance()
    }
    lateinit var downloadUrl:String

    lateinit var myCalendar: Calendar
    lateinit var dateSetListener:DatePickerDialog.OnDateSetListener
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var progressBar: ProgressDialog
    private lateinit var countryCode:String
    private lateinit var phoneNumber:String
    private lateinit var mProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(requireActivity()).load(R.drawable.pic).circleCrop().into(binding.ivProfile)

        (activity as MainActivity).supportActionBar!!.title = "My Account"

        binding.btnEditPic.setOnClickListener {
            showBottomSheetDialog()
        }
        binding.etDOB.setOnClickListener {
            DatePickerDialog(this.requireContext(),DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                binding.etDOB.setText("$dayOfMonth/${month+1}/$year")
            },1999,7,8).show()
        }


        binding.btnSave.setOnClickListener {
            val name:String = binding.etName.text.toString()
            val email:String = binding.etEmail.text.toString()
            val number:String = binding.etContactCode.text.toString()

            val user = com.example.e_patrakaar.database.base.User(name,downloadUrl,email,number,auth.uid!!)
            database.collection("users").document(auth.uid!!).set(user).addOnSuccessListener {

            }
            saveInfo()
        }

        binding.etContactCode.addTextChangedListener {
            binding.btnVerify.isEnabled = !(it.isNullOrBlank() ||  it.length < 10 || it.length > 10)
        }
        binding.btnVerify.setOnClickListener {
            checkNumber()
            mProgressBar.visibility = View.VISIBLE
        }


    }

    private fun checkNumber() {
        phoneNumber = binding.etContactCode.text.trim().toString()

        if (phoneNumber.isEmpty()){
            if (phoneNumber.length == 10){

                countryCode = binding.etContact.selectedCountryCodeWithPlus
                phoneNumber = countryCode+binding.etContactCode.text.toString()

                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNumber)       // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this.requireActivity())                 // Activity (for callback binding)
                    .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)

            }else{
                Toast.makeText(this.requireContext(),"Please Enter Correct Number", Toast.LENGTH_SHORT).show()
            }

        }else{
            Toast.makeText(this.requireContext(),"Please Enter Number", Toast.LENGTH_SHORT).show()

        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = task.result?.user
                    Toast.makeText(
                        this.requireContext(),
                        "Authenticate Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    sendBackToEditProfile()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }
    private fun sendBackToEditProfile(){
        // Now you have to write the code for going back to EditProfile

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
            // Save verification ID and resending token so we can use them later
            val  intent = Intent(this@EditProfileFragment.requireContext(),OtpActivity::class.java)
            intent.putExtra("OTP" , verificationId)
            intent.putExtra("resendToken", token)
            intent.putExtra("phonNumber", phoneNumber)

            startActivity(intent)
            mProgressBar.visibility=View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser!= null){
            binding.btnVerify.isEnabled = false
            Toast.makeText(this.requireContext(),"Already Verified",Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveInfo() {
        progressBar = ProgressDialog(requireActivity())
        progressBar.setMessage("Saving your info....")
        progressBar.show()
        Handler(Looper.getMainLooper()).postDelayed({
            progressBar.dismiss()
            findNavController().navigateUp()
        }, 1500)
    }

    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(requireActivity())
        val view = CustomBottomSheet2Binding.inflate(layoutInflater)

        view.tvEditPhoto.setOnClickListener {
            pickImageFromGallery()
            Toast.makeText(requireActivity(), "Edit photo is accessed.", Toast.LENGTH_SHORT)
                .show()
            dialog.dismiss()
        }
        view.tvRemove.setOnClickListener {
            Toast.makeText(requireActivity(), "Remove photo is accessed.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.setCancelable(true)
        dialog.setContentView(view.root)
        dialog.show()
    }

    private fun pickImageFromGallery() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1000){
            data?.data?.let {
                binding.ivProfile.setImageURI(it)
                uploadImage(it)
            }
        }
    }

    private fun uploadImage(it: Uri) {
        binding.btnSave.isEnabled = false

        val ref = storage.reference.child("uploads/"+auth.uid.toString())

        val uploadTask = ref.putFile(it)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->

            if (!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl

        }) .addOnCompleteListener { task ->
            binding.btnSave.isEnabled = true
            if (task.isSuccessful){
                downloadUrl = task.result.toString()
                Log.i("URL","downloadUrl: $downloadUrl")
            }else{

            }
        }.addOnFailureListener {

            binding.btnSave.isEnabled = true
       }

    }
}