package com.example.schoolbustrackerparent.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.schoolbustrackerparent.R
import com.example.schoolbustrackerparent.databinding.FragmentSignUpBinding
import com.example.schoolbustrackerparent.util.AuthEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    private val viewModel: AuthViewModel by activityViewModels()
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding
    private val TAG = "SignUpFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignUpBinding.bind(view)
        // (activity as MainActivity).setBottomNavVisibilityGone()
        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.title = "Sign Up"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        // (activity as MainActivity).setBottomNavVisibilityGone()
        setupListeners()
        listenToChannels()
        return binding?.root
    }

    private fun setupListeners() {
        binding?.apply {
            buttonSignUp.setOnClickListener {
                val studentNumber = editTextStudentNumber.text.toString()
                val phoneNumber = editTextPhoneNumber.text.toString()
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()
                val confirmPassword = editTextConfirmPassword.text.toString()

                if (studentNumber.isEmpty()) {
                    editTextStudentNumber.error = "Student Number should not be empty"
                    return@setOnClickListener
                }

                if (phoneNumber.isEmpty()) {
                    editTextPhoneNumber.error = "Phone Number should not be empty"
                    return@setOnClickListener
                }

                if (email.isEmpty()) {
                    editTextEmail.error = "Email should not be empty"
                    return@setOnClickListener
                }

                if (password.isEmpty()) {
                    editTextPassword.error = "Password should not be empty"
                    return@setOnClickListener
                }

                if (confirmPassword.isEmpty()) {
                    editTextConfirmPassword.error = "Confirm Password should not be empty"
                    return@setOnClickListener
                }

                if (password != confirmPassword) {
                    editTextConfirmPassword.error = "Passwords do not match"
                    return@setOnClickListener
                }

                viewModel.signUpUser(
                    studentNumber.toInt(),
                    phoneNumber.toLong(),
                    email,
                    password,
                    confirmPassword
                )

                /* viewModel.currentUser.observe(viewLifecycleOwner) { firebaseUser ->
                     if (firebaseUser != null) {
                         viewModel.saveUser(driver)
                     }
                 }*/

            }

            textViewSignIn.setOnClickListener {
                findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
            }
        }
    }

    private fun listenToChannels() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allEventsFlow.collect { event ->
                when (event) {
                    is AuthEvents.Error -> {
                        binding?.apply {
                            textViewErrorSignUp.text = event.error
                        }
                    }

                    is AuthEvents.Message -> {
                        if (event.message == "sign up success") {
                            // findNavController().navigate(R.id.action_signUpFragment_to_attendanceFragment)
                            Toast.makeText(
                                requireContext(),
                                "Login success!!!!!!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    else -> {
                        Log.d(TAG, "listenToChannels: No event received so far")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}