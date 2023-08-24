package com.example.schoolbustrackerparent.data.datasource.notification

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NotificationDataSource {

    override suspend fun saveFCMTokenToFirestore(userEmail: String, token: String): Boolean {
        try {
            val userData = hashMapOf(
                "fcm_token" to token
            )

            val documentRef = firestore.collection("parents")
                .document(userEmail)

            val updateTask = documentRef.update(userData as Map<String, Any>)

            updateTask.await()

            return updateTask.isSuccessful
        } catch (e: Exception) {
            Log.e("NotificationDataSource", "Error: ${e.message}")
            return false
        }
    }

    override suspend fun getFCMToken(userEmail: String): String? {
        try {
            val document = firestore.collection("parents")
                .document(userEmail)
                .get()
                .await()

            return document.getString("fcm_token")
        } catch (e: Exception) {
            return null
        }
    }


}