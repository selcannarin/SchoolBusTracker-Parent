package com.example.schoolbustrackerparent.data.repository.driver

import android.net.Uri
import com.example.schoolbustrackerparent.data.datasource.driver.DriverDataSource
import com.example.schoolbustrackerparent.data.model.Driver
import com.example.schoolbustrackerparent.util.UiState
import javax.inject.Inject

class DriverRepositoryImpl @Inject constructor(
    private val dataSource: DriverDataSource
) : DriverRepository {

    override suspend fun getDriver(parentEmail: String, result: (UiState<Driver>) -> Unit) {
        return dataSource.getDriver(parentEmail, result)
    }

    override suspend fun getProfilePhoto(driver: Driver, result: (UiState<Uri?>) -> Unit) {
        return dataSource.getProfilePhoto(driver, result)
    }

    override suspend fun getLicencePlateFile(driver: Driver, result: (UiState<Uri?>) -> Unit) {
        return dataSource.getLicencePlateFile(driver, result)
    }
}