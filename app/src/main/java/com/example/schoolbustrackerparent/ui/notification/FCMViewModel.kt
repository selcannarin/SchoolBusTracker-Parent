package com.example.schoolbustrackerparent.ui.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolbustrackerparent.data.repository.notification.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FCMViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val TAG = "FCMViewModel"

    fun onTokenRefresh(token: String) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val email = currentUser.email
            if (email != null) {
                saveFCMTokenToFirestore(email, token)
            } else {
                Log.e(TAG, "There is no user email.")
            }
        } else {
            Log.e(TAG, "User is not signed in or created account.")
        }
    }

    private fun saveFCMTokenToFirestore(email: String, token: String) {
        viewModelScope.launch {
            val success = notificationRepository.saveFCMTokenToFirestore(email, token)
            if (success) {
                Log.d(TAG, "FCM Token is saved successfully.")
            } else {
                Log.d(TAG, "Failed to save FCM Token.")
            }
        }
    }

    fun onUserLoginSuccess(email: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                saveFCMTokenToFirestore(email, token)
            } else {
                Log.e(TAG, "Failed to Retrieve User's FCM ID: ${task.exception?.message}")
            }
        }
    }
}
