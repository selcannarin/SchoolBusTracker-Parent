package com.example.schoolbustrackerparent.data.datasource.location

import android.util.Log
import com.example.schoolbustrackerparent.data.model.Location
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "LocationDataSource"

class LocationDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : LocationDataSource {

    override fun getLatestBusLocation(driverEmail: String, callback: (Location?) -> Unit) {
        val driverRef = firestore.collection("locations").document(driverEmail)
        driverRef.collection("timestamps")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val querySnapshot = task.result
                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                        val latestLocation =
                            querySnapshot.documents[0].toObject(Location::class.java)
                        callback(latestLocation)
                    } else {
                        Log.d(TAG, "Document not found or empty.")
                        callback(null)
                    }
                } else {
                    val exception = task.exception
                    Log.e(TAG, "Firestore query failed: $exception")
                    callback(null)
                }
            }
    }


    override suspend fun getStudentAddressByNumber(studentNumber: Int, result: (String?) -> Unit) {
        try {
            val query = firestore.collection("student")
                .whereEqualTo("student_number", studentNumber)
                .limit(1)

            val queryResult = query.get().await()

            if (!queryResult.isEmpty) {
                val document = queryResult.documents[0]
                val address = document.getString("address")
                result(address)
            } else {
                result(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            result(null)
        }
    }

}