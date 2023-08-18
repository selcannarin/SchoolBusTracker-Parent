package com.example.schoolbustrackerparent.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.schoolbustrackerparent.R
import com.example.schoolbustrackerparent.databinding.FragmentSignInBinding
import com.example.schoolbustrackerparent.util.AuthEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private val viewModel: AuthViewModel by activityViewModels()
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignInBinding.bind(view)
        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.title = "Sign In"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        setupListeners()
        listenToChannels()
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
        return binding?.root
    }

    private fun setupListeners() {
        binding?.apply {
            buttonSignIn.setOnClickListener {
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()

                if (email.isEmpty()) {
                    editTextEmail.error = "Email should not be empty"
                    return@setOnClickListener
                }

                if (password.isEmpty()) {
                    editTextPassword.error = "Password should not be empty"
                    return@setOnClickListener
                }

                viewModel.signInUser(email, password)
            }

            textViewSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
            }

            textViewForgotPassword.setOnClickListener {
                findNavController().navigate(R.id.action_signInFragment_to_resetPasswordFragment)
            }

        }
    }

    private fun listenToChannels() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allEventsFlow.collect { event ->
                when (event) {
                    is AuthEvents.Error -> {
                        binding?.apply {
                            textViewErrorSignIn.text = event.error
                        }
                    }

                    is AuthEvents.Message -> {
                        if (event.message == "login success") {
                            Toast.makeText(
                                requireContext(),
                                "Login success!!!!!!",
                                Toast.LENGTH_LONG
                            ).show()
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
                        }
                    }

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
