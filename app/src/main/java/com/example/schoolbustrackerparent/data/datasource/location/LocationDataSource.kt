package com.example.schoolbustrackerparent.data.datasource.location

import com.example.schoolbustrackerparent.data.model.Location

interface LocationDataSource {

    fun getLatestBusLocation(driverEmail: String, callback: (Location?) -> Unit)

    suspend fun getStudentAddressByNumber(studentNumber: Int, result: (String?) -> Unit)

}