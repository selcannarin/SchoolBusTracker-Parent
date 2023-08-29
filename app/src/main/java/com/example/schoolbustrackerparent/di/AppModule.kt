package com.example.schoolbustrackerparent.di

import com.example.schoolbustrackerparent.data.datasource.auth.AuthDataSource
import com.example.schoolbustrackerparent.data.datasource.auth.AuthDataSourceImpl
import com.example.schoolbustrackerparent.data.datasource.driver.DriverDataSource
import com.example.schoolbustrackerparent.data.datasource.driver.DriverDataSourceImpl
import com.example.schoolbustrackerparent.data.datasource.location.LocationDataSource
import com.example.schoolbustrackerparent.data.datasource.location.LocationDataSourceImpl
import com.example.schoolbustrackerparent.data.datasource.notification.NotificationDataSource
import com.example.schoolbustrackerparent.data.datasource.notification.NotificationDataSourceImpl
import com.example.schoolbustrackerparent.data.datasource.student.StudentDataSource
import com.example.schoolbustrackerparent.data.datasource.student.StudentDataSourceImpl
import com.example.schoolbustrackerparent.data.repository.auth.AuthRepository
import com.example.schoolbustrackerparent.data.repository.auth.AuthRepositoryImpl
import com.example.schoolbustrackerparent.data.repository.driver.DriverRepository
import com.example.schoolbustrackerparent.data.repository.driver.DriverRepositoryImpl
import com.example.schoolbustrackerparent.data.repository.location.LocationRepository
import com.example.schoolbustrackerparent.data.repository.location.LocationRepositoryImpl
import com.example.schoolbustrackerparent.data.repository.notification.NotificationRepository
import com.example.schoolbustrackerparent.data.repository.notification.NotificationRepositoryImpl
import com.example.schoolbustrackerparent.data.repository.student.StudentRepository
import com.example.schoolbustrackerparent.data.repository.student.StudentRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }


    @Singleton
    @Provides
    fun provideAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideAuthDataSource(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthDataSource {
        return AuthDataSourceImpl(auth, firestore)
    }

    @Singleton
    @Provides
    fun provideAuthRepository(
        authenticator: AuthDataSource
    ): AuthRepository {
        return AuthRepositoryImpl(authenticator)
    }

    @Singleton
    @Provides
    fun provideDriverDataSource(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): DriverDataSource {
        return DriverDataSourceImpl(firestore, storage)
    }

    @Singleton
    @Provides
    fun provideDriverRepository(
        driverDataSource: DriverDataSource
    ): DriverRepository {
        return DriverRepositoryImpl(driverDataSource)
    }

    @Singleton
    @Provides
    fun getStudentDataSource(
        firestore: FirebaseFirestore
    ): StudentDataSource {
        return StudentDataSourceImpl(firestore)
    }

    @Singleton
    @Provides
    fun getStudentRepository(
        dataSource: StudentDataSource
    ): StudentRepository {
        return StudentRepositoryImpl(dataSource)
    }

    @Singleton
    @Provides
    fun getNotificationDataSource(
        firestore: FirebaseFirestore
    ): NotificationDataSource {
        return NotificationDataSourceImpl(firestore)
    }

    @Singleton
    @Provides
    fun getNotificationRepository(
        dataSource: NotificationDataSource
    ): NotificationRepository {
        return NotificationRepositoryImpl(dataSource)
    }

    @Singleton
    @Provides
    fun getLocationDataSource(
        firestore: FirebaseFirestore
    ): LocationDataSource {
        return LocationDataSourceImpl(firestore)
    }

    @Singleton
    @Provides
    fun getLocationRepository(
        dataSource: LocationDataSource
    ): LocationRepository {
        return LocationRepositoryImpl(dataSource)
    }

}