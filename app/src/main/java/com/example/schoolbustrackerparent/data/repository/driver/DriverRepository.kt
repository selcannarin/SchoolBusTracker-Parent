package com.example.schoolbustrackerparent.data.repository.driver

import android.net.Uri
import com.example.schoolbustrackerparent.data.model.Driver
import com.example.schoolbustrackerparent.util.UiState

interface DriverRepository {

    suspend fun getDriver(studentNumber: Int, result: (UiState<Driver>) -> Unit)

    suspend fun getProfilePhoto(driver: Driver, result: (UiState<Uri?>) -> Unit)

    suspend fun getLicencePlateFile(driver: Driver, result: (UiState<Uri?>) -> Unit)
}