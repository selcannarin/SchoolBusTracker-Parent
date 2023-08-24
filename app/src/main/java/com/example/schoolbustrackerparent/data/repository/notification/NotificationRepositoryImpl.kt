package com.example.schoolbustrackerparent.data.repository.notification

import com.example.schoolbustrackerparent.data.datasource.notification.NotificationDataSource
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val dataSource: NotificationDataSource
):NotificationRepository{
    override suspend fun saveFCMTokenToFirestore(userEmail: String, token: String): Boolean {
        return dataSource.saveFCMTokenToFirestore(userEmail,token)
    }

    override suspend fun getFCMToken(userEmail: String): String? {
        return dataSource.getFCMToken(userEmail)
    }

}