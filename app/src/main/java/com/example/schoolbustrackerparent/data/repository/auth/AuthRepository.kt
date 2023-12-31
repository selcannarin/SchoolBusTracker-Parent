package com.example.schoolbustrackerparent.data.repository.auth

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {

    suspend fun signUpWithStudentNumberAndPhone(
        studentNumber: Int,
        parentPhone: Long,
        email: String,
        password: String
    ): FirebaseUser?

    suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser?

    suspend fun saveUser(studentNumber: Int, parentEmail: String): Boolean

    fun signOut(): FirebaseUser?

    fun getUser(): FirebaseUser?

    suspend fun sendPasswordReset(email: String): Boolean


}