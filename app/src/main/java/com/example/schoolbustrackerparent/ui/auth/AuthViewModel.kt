package com.example.schoolbustrackerparent.ui.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolbustrackerparent.data.repository.auth.AuthRepository
import com.example.schoolbustrackerparent.util.AuthEvents
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

    private val TAG = "AuthViewModel"
    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val currentUser get() = _firebaseUser

    private val eventsChannel = Channel<AuthEvents>()

    val allEventsFlow = eventsChannel.receiveAsFlow()

    fun signInUser(email: String, password: String) = viewModelScope.launch {
        when {
            email.isEmpty() -> {
                eventsChannel.send(AuthEvents.ErrorCode(1))
            }

            password.isEmpty() -> {
                eventsChannel.send(AuthEvents.ErrorCode(2))
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
            (studentNumber == null) -> {
                eventsChannel.send(AuthEvents.ErrorCode(1))
            }

            (phoneNumber == null) -> {
                eventsChannel.send(AuthEvents.ErrorCode(2))
            }

            email.isEmpty() -> {
                eventsChannel.send(AuthEvents.ErrorCode(3))
            }

            password.isEmpty() -> {
                eventsChannel.send(AuthEvents.ErrorCode(4))
            }

            password != confirmPass -> {
                eventsChannel.send(AuthEvents.ErrorCode(5))
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
        } catch (e: Exception) {
            val error = e.toString().split(":").toTypedArray()
            Log.d(TAG, "signInUser: ${error[1]}")
            eventsChannel.send(AuthEvents.Error(error[1]))
        }
    }

    private fun actualSignUpUser(
        studentNumber: Int,
        phoneNumber: Long,
        email: String,
        password: String
    ) = viewModelScope.launch {
        try {
            val user = repository.signUpWithStudentNumberAndPhone(
                studentNumber,
                phoneNumber,
                email,
                password
            )
            user?.let {
                _firebaseUser.postValue(it)
                eventsChannel.send(AuthEvents.Message("sign up success"))
            }
        } catch (e: Exception) {
            val error = e.toString().split(":").toTypedArray()
            Log.d(TAG, "signInUser: ${error[1]}")
            eventsChannel.send(AuthEvents.Error(error[1]))
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
            Log.d(TAG, "signInUser: ${error[1]}")
            eventsChannel.send(AuthEvents.Error(error[1]))
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
            Log.d(TAG, "signInUser: ${error[1]}")
            eventsChannel.send(AuthEvents.Error(error[1]))
        }
    }


}