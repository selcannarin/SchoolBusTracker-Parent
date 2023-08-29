package com.example.schoolbustrackerparent.data.repository.location

import com.example.schoolbustrackerparent.data.datasource.location.LocationDataSource
import com.example.schoolbustrackerparent.data.model.Location
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val dataSource: LocationDataSource
) : LocationRepository {
    override fun getLatestBusLocation(driverEmail: String, callback: (Location?) -> Unit) {
        return dataSource.getLatestBusLocation(driverEmail, callback)
    }

    override suspend fun getStudentAddressByNumber(studentNumber: Int, result: (String?) -> Unit) {
        return dataSource.getStudentAddressByNumber(studentNumber, result)
    }

}