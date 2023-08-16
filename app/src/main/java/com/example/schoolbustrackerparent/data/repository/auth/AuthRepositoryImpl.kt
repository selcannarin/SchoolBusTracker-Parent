package com.example.schoolbustrackerparent.data.repository.auth

import com.example.schoolbustrackerparent.data.datasource.auth.AuthDataSource
import com.example.schoolbustrackerparent.data.model.Parent
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataSource: AuthDataSource
) : AuthRepository {
    override suspend fun signUpWithStudentNumberAndPhone(
        studentNumber: Int,
        parentPhone: Long,
        email: String,
        password: String
    ): FirebaseUser? {
        return dataSource.signUpWithStudentNumberAndPhone(
            studentNumber,
            parentPhone,
            email,
            password
        )
    }

    override suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser? {
        return dataSource.signInWithEmailPassword(email, password)
    }

    override suspend fun saveUser(parent: Parent): Boolean {
        return dataSource.saveUser(parent)
    }

    override fun signOut(): FirebaseUser? {
        return dataSource.signOut()
    }

    override fun getUser(): FirebaseUser? {
        return dataSource.getUser()
    }

    override suspend fun sendPasswordReset(email: String): Boolean {
        dataSource.sendPasswordReset(email)
        return true
    }
}