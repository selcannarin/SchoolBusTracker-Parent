package com.example.schoolbustrackerparent.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolbustrackerparent.data.repository.auth.AuthRepository
import com.example.schoolbustrackerparent.util.AuthEvents
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val currentUser get() = _firebaseUser

    private val eventsChannel = Channel<AuthEvents>()

    val allEventsFlow = eventsChannel.receiveAsFlow()

    fun signInUser(email: String, password: String) = viewModelScope.launch {
        when {
            email.isEmpty() -> {
                eventsChannel.send(AuthEvents.ErrorCode(3))
            }

            password.isEmpty() -> {
                eventsChannel.send(AuthEvents.ErrorCode(4))
            }

            else -> {
                actualSignInUser(email, password)
            }
        }
    }

    fun signUpUser(
        studentNumber: Int,
        phoneNumber: Long,
        email: String,
        password: String,
        confirmPass: String
    ) = viewModelScope.launch {
        when {
            studentNumber <= 0 -> {
                eventsChannel.send(AuthEvents.ErrorCode(4))
            }

            phoneNumber <= 0 -> {
                eventsChannel.send(AuthEvents.ErrorCode(5))
            }

            email.isEmpty() -> {
                eventsChannel.send(AuthEvents.ErrorCode(1))
            }

            password.isEmpty() -> {
                eventsChannel.send(AuthEvents.ErrorCode(2))
            }

            password != confirmPass -> {
                eventsChannel.send(AuthEvents.ErrorCode(3))
            }

            else -> {
                actualSignUpUser(studentNumber, phoneNumber, email, password)
            }
        }
    }

    private fun actualSignInUser(email: String, password: String) = viewModelScope.launch {
        try {
            val user = repository.signInWithEmailPassword(email, password)
            user?.let {
                _firebaseUser.postValue(it)
                eventsChannel.send(AuthEvents.Message("login success"))
            }
        } catch (e: FirebaseAuthException) {
            val errorMessage = e.message
            errorMessage?.let { AuthEvents.Error(it) }?.let { eventsChannel.send(it) }
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Sign in failed"
            eventsChannel.send(AuthEvents.Error(errorMessage))
        }
    }

    private suspend fun actualSignUpUser(
        studentNumber: Int,
        phoneNumber: Long,
        email: String,
        password: String
    ) {
        try {
            val user = repository.signUpWithStudentNumberAndPhone(
                studentNumber,
                phoneNumber,
                email,
                password
            )
            user?.let {
                _firebaseUser.postValue(user)
                eventsChannel.send(AuthEvents.Message("sign up success"))
            }
        } catch (e: FirebaseAuthException) {

            val errorMessage = e.message ?: "Sign up failed"
            eventsChannel.send(AuthEvents.Error(errorMessage))

        } catch (e: Exception) {
            val errorMessage = e.message ?: "Sign up failed"
            eventsChannel.send(AuthEvents.Error(errorMessage))
        }
    }

    fun signOut() = viewModelScope.launch {
        try {
            val user = repository.signOut()
            user?.let {
                eventsChannel.send(AuthEvents.Message("sign out failure"))
            } ?: eventsChannel.send(AuthEvents.Message("sign out successful"))

            getCurrentUser()

        } catch (e: Exception) {
            val error = e.toString().split(":").toTypedArray()
            eventsChannel.send(AuthEvents.Error(error[1]))
        }
    }

    fun saveUser(studentNumber: Int, parentPhone: Long, parentEmail: String) =
        viewModelScope.launch {
            val success = repository.saveUser(studentNumber, parentPhone, parentEmail)
            if (success) {
                eventsChannel.send(AuthEvents.Message("User saved successfully"))
            } else {
                eventsChannel.send(AuthEvents.Error("Failed to save user"))
            }
        }

    fun getCurrentUser() = viewModelScope.launch {
        val user = repository.getUser()
        _firebaseUser.postValue(user)
    }

    fun verifySendPasswordReset(email: String) {
        if (email.isEmpty()) {
            viewModelScope.launch {
                eventsChannel.send(AuthEvents.ErrorCode(1))
            }
        } else {
            sendPasswordResetEmail(email)
        }

    }

    private fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
        try {
            val result = repository.sendPasswordReset(email)
            if (result) {
                eventsChannel.send(AuthEvents.Message("Reset email sent"))
            } else {
                eventsChannel.send(AuthEvents.Error("Could not send password reset"))
            }
        } catch (e: Exception) {
            val error = e.toString().split(":").toTypedArray()
            eventsChannel.send(AuthEvents.Error(error[1]))
        }
    }


}