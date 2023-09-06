package com.example.schoolbustrackerparent.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.schoolbustrackerparent.R
import com.example.schoolbustrackerparent.databinding.FragmentSignInBinding
import com.example.schoolbustrackerparent.ui.MainActivity
import com.example.schoolbustrackerparent.ui.notification.FCMViewModel
import com.example.schoolbustrackerparent.util.AuthEvents
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private var authStateListener: FirebaseAuth.AuthStateListener? = null
    private val viewModel: AuthViewModel by activityViewModels()
    private val fcmViewModel: FCMViewModel by activityViewModels()
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignInBinding.bind(view)
        (requireActivity() as MainActivity).setToolbarTitle("Sign In")
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

                fcmViewModel.onUserLoginSuccess(email)
            }


            textViewSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
            }

            textViewForgotPassword.setOnClickListener {
                findNavController().navigate(R.id.action_signInFragment_to_resetPasswordFragment)
            }

        }
    }

    private var isNavigationPerformed = false
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
                        if (event.message == "login success" && !isNavigationPerformed) {
                            isNavigationPerformed = true
                            Toast.makeText(
                                requireContext(),
                                "Login success!",
                                Toast.LENGTH_LONG
                            ).show()
                            findNavController().navigate(R.id.action_signInFragment_to_busLocationFragment)
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
    private fun startAuthStateListener() {
        if (authStateListener == null) {
            authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                Log.i("firebase", "AuthState changed to ${firebaseAuth.currentUser?.uid}")
                if (firebaseAuth.currentUser != null && !isNavigationPerformed) {
                    isNavigationPerformed = true
                    findNavController().navigate(R.id.action_signInFragment_to_busLocationFragment)
                }
            }
            firebaseAuth.addAuthStateListener(authStateListener!!)
        }
    }

    private fun stopAuthStateListener() {
        authStateListener?.let { firebaseAuth.removeAuthStateListener(it) }
    }

    override fun onResume() {
        super.onResume()
        startAuthStateListener()
        (requireActivity() as MainActivity).setBottomNavVisibilityGone()
        (requireActivity() as MainActivity).hideNavigationDrawer()
    }

    override fun onPause() {
        super.onPause()
        stopAuthStateListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
