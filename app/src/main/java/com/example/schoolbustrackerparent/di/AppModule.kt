package com.example.schoolbustrackerparent.di

import com.example.schoolbustrackerparent.data.datasource.auth.AuthDataSource
import com.example.schoolbustrackerparent.data.datasource.auth.AuthDataSourceImpl
import com.example.schoolbustrackerparent.data.repository.auth.AuthRepository
import com.example.schoolbustrackerparent.data.repository.auth.AuthRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    fun provideAuthDataSource(auth: FirebaseAuth, firestore: FirebaseFirestore): AuthDataSource {
        return AuthDataSourceImpl(auth, firestore)
    }

    @Singleton
    @Provides
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideAuthRepository(
        authenticator: AuthDataSource
    ): AuthRepository {
        return AuthRepositoryImpl(authenticator)
    }

}