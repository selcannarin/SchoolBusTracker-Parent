package com.example.schoolbustrackerparent.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.schoolbustrackerparent.R
import com.example.schoolbustrackerparent.databinding.FragmentSignUpBinding
import com.example.schoolbustrackerparent.ui.MainActivity
import com.example.schoolbustrackerparent.ui.notification.FCMViewModel
import com.example.schoolbustrackerparent.util.AuthEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    private val viewModel: AuthViewModel by activityViewModels()
    private var _binding: FragmentSignUpBinding? = null
    private val fcmViewModel: FCMViewModel by activityViewModels()
    private val binding get() = _binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignUpBinding.bind(view)
        (activity as MainActivity).setBottomNavVisibilityGone()
        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.title = "Sign Up"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        (activity as MainActivity).setBottomNavVisibilityGone()
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

                if (studentNumber.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() ||
                    password.isEmpty() || confirmPassword.isEmpty()
                ) {
                    textViewErrorSignUp.text = "Please fill in all fields"
                    return@setOnClickListener
                }

                if (studentNumber.toInt() <= 0) {
                    editTextStudentNumber.error = "Invalid student number"
                    return@setOnClickListener
                }

                if (phoneNumber.toLong() <= 0) {
                    editTextPhoneNumber.error = "Invalid phone number"
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

                viewModel.currentUser.observe(viewLifecycleOwner) { firebaseUser ->
                    if (firebaseUser != null) {
                        viewModel.saveUser(studentNumber.toInt(), email)
                        fcmViewModel.onUserLoginSuccess(email)

                        findNavController().navigate(R.id.action_signUpFragment_to_busLocationFragment)
                    }
                }

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

                        }
                    }

                    is AuthEvents.ErrorCode -> {
                        binding?.apply {
                            if (event.code == 1) {
                                editTextEmail.error = "Email should not be empty"
                            }
                            if (event.code == 2) {
                                editTextPassword.error = "Password should not be empty"
                            }
                            if (event.code == 3) {
                                editTextPassword.error = "Passwords do not match"
                            }

                            if (event.code == 4) {
                                editTextPassword.error = "Invalid student number"
                            }
                            if (event.code == 5) {
                                editTextPassword.error = "Invalid phone number"
                            }
                        }
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
