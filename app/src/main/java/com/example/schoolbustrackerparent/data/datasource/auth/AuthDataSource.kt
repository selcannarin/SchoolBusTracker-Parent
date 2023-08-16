package com.example.schoolbustrackerparent.data.datasource.auth

import com.example.schoolbustrackerparent.data.model.Parent
import com.google.firebase.auth.FirebaseUser

interface AuthDataSource {

    suspend fun signUpWithStudentNumberAndPhone(
        studentNumber: Int,
        parentPhone: Long,
        email: String,
        password: String
    ): FirebaseUser?

    suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser?

    suspend fun saveUser(parent: Parent): Boolean

    fun signOut(): FirebaseUser?

    fun getUser(): FirebaseUser?

    suspend fun sendPasswordReset(email: String)

}