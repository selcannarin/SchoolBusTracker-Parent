package com.example.schoolbustrackerparent.data.repository.notification

interface NotificationRepository {

    suspend fun saveFCMTokenToFirestore(userEmail: String, token: String): Boolean

    suspend fun getFCMToken(userEmail: String): String?
}