package com.example.schoolbustrackerparent.data.datasource.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
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
        try {
            val studentDocumentRef = firestore.collection("students")
                .document(studentNumber.toString())

            val documentSnapshot = studentDocumentRef.get().await()

            if (documentSnapshot.exists()) {
                val parentPhoneNumber = documentSnapshot.getLong("parent_phone_number")

                if (parentPhoneNumber == parentPhone && documentSnapshot.id == studentNumber.toString()) {
                    try {
                        val authResult =
                            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                        return authResult.user
                    } catch (e: FirebaseAuthException) {
                        throw Exception(e.localizedMessage)
                    }
                } else {
                    throw Exception("Parent phone number or student information is incorrect.")
                }
            } else {
                throw Exception("Student not found or provided information is incorrect.")
            }
        } catch (e: FirebaseFirestoreException) {
            throw Exception(e.localizedMessage)
        }
        return null
    }

    override suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser? {
        try {
            val parentDocument = firestore.collection("parents")
                .document(email)
                .get()
                .await()

            if (parentDocument.exists()) {
                val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                return authResult.user
            } else {
                throw FirebaseAuthException("user-not-found", "User not found.")
            }
        } catch (e: FirebaseAuthException) {
            throw e
        } catch (e: Exception) {
            throw Exception(e.localizedMessage)
        }
    }

    override suspend fun saveUser(
        studentNumber: Int,
        parentEmail: String
    ): Boolean {
        return try {
            val parentData = hashMapOf(
                "student_number" to studentNumber
            )
            firestore.collection("parents").document(parentEmail).set(parentData).await()
            true

        } catch (e: Exception) {
            return false
        }
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