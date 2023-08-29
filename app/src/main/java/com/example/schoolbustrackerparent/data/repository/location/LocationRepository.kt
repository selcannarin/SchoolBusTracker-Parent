package com.example.schoolbustrackerparent.data.repository.location

import com.example.schoolbustrackerparent.data.model.Location

interface LocationRepository {

    fun getLatestBusLocation(driverEmail: String, callback: (Location?) -> Unit)

    suspend fun getStudentAddressByNumber(studentNumber: Int, result: (String?) -> Unit)
}