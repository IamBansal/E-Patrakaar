package com.example.e_patrakaar.view.fragment.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.e_patrakaar.R
import com.example.e_patrakaar.databinding.FragmentAuthOptionsBinding
import com.example.e_patrakaar.utils.Constants.TAG
import com.example.e_patrakaar.view.activity.AuthActivity
import com.example.e_patrakaar.view.activity.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthOptionsFragment : Fragment() {

    private lateinit var binding: FragmentAuthOptionsBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseApp.initializeApp(requireActivity())

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        auth = FirebaseAuth.getInstance()

        binding.btnGoogle.setOnClickListener {
            loginWithGoogle()
        }

        binding.btnPhone.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_options_to_navigation_phone)
        }

        binding.tvSkip.setOnClickListener {
            guestLogin()
        }

    }

    private fun guestLogin() {

        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("Guest Login")
            .setMessage("You are exploring as a guest, this account will be valid for 30 days from now.")
            .setPositiveButton("Continue as a guest"){_,_->

                val dialog = (activity as AuthActivity).setProgressDialog(requireContext(), "Signing you in...")
                dialog.show()

                auth.signInAnonymously().addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInAnonymously:success")
                        dialog.dismiss()
                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        (activity as AuthActivity).finish()
                    } else {
                        Log.w(TAG, "signInAnonymously:failure", task.exception)
                        Toast.makeText(requireActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }
            .setNegativeButton("Cancel"){_,_->}
            .create()
            .show()
    }

    private fun loginWithGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            handleResult(task)
        } else {
//            Toast.makeText(requireActivity(), "Result Not okay", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        if (completedTask.isSuccessful) {
            val account: GoogleSignInAccount? = completedTask.result
            if (account != null) {
                val dialog = (activity as AuthActivity).setProgressDialog(requireContext(), "Signing you in...")
                dialog.show()
                updateUI(account, dialog)
            }
        } else{
            Toast.makeText(requireActivity(), completedTask.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount, dialog: AlertDialog) {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    dialog.dismiss()
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    Toast.makeText(requireActivity(), "Sign in successful with\nUsername : ${account.displayName}\nEmail : ${account.email}", Toast.LENGTH_SHORT).show()
                    (activity as AuthActivity).finish()
                } else {
                    Toast.makeText(requireActivity(), task.exception.toString(), Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
        }
    }
}