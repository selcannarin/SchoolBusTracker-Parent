package com.example.schoolbustrackerparent.data.datasource.auth

import com.example.schoolbustrackerparent.data.model.Parent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthDataSource {
    override suspend fun signUpWithStudentNumberAndPhone(
        studentNumber: Int,
        parentPhone: Long,
        email: String,
        password: String
    ): FirebaseUser? {
        val query = firestore.collection("students")
            .document(studentNumber.toString())

        val documentSnapshot = query.get().await()

        if (documentSnapshot.exists()) {
            val parentPhoneNumber = documentSnapshot.getLong("parent_phone_number")

            // parent_phone_number ve belgenin kendi adı doğruysa kaydı oluştur
            if (parentPhoneNumber == parentPhone && documentSnapshot.id == studentNumber.toString()) {
                try {
                    // Firebase Authentication ile kayıt oluşturma
                    val authResult =
                        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                    return authResult.user
                } catch (e: Exception) {
                    return null
                }
            }
        }

        // Öğrenci bulunamadı veya giriş bilgileri yanlış
        return null
    }

    override suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser? {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            authResult.user
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveUser(parent: Parent): Boolean {
        TODO("Not yet implemented")
    }

    override fun signOut(): FirebaseUser? {
        firebaseAuth.signOut()
        return firebaseAuth.currentUser
    }

    override fun getUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override suspend fun sendPasswordReset(email: String) {

        firebaseAuth.sendPasswordResetEmail(email).await()

    }

}