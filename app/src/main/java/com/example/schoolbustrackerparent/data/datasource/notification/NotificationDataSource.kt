package com.example.schoolbustrackerparent.data.datasource.notification

interface NotificationDataSource {

    suspend fun saveFCMTokenToFirestore(userEmail: String, token: String): Boolean

    suspend fun getFCMToken(userEmail: String): String?

}