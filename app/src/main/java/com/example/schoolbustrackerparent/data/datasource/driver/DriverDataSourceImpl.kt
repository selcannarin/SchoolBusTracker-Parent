package com.example.schoolbustrackerparent.data.datasource.driver

import android.net.Uri
import com.example.schoolbustrackerparent.data.model.Driver
import com.example.schoolbustrackerparent.util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DriverDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : DriverDataSource {

    override suspend fun getDriver(parentEmail: String, result: (UiState<Driver>) -> Unit) {
        try {
            val parentDocument = firestore.collection("parents").document(parentEmail).get().await()

            if (parentDocument.exists()) {
                val studentNumber = parentDocument.getLong("student_number")

                if (studentNumber != null) {
                    val driverQuery =
                        firestore.collection("drivers")
                            .whereArrayContains("students", studentNumber)
                    val driverSnapshot = driverQuery.get().await()

                    if (!driverSnapshot.isEmpty) {
                        val driverDocument = driverSnapshot.documents[0]
                        val driver = driverDocument.toObject(Driver::class.java)

                        if (driver != null) {
                            result(UiState.Success(driver))
                        } else {
                            result(UiState.Failure("Failed to parse driver data"))
                        }
                    } else {
                        result(UiState.Failure("Driver not found for student $studentNumber"))
                    }
                } else {
                    result(UiState.Failure("Student number not found for email $parentEmail"))
                }
            } else {
                result(UiState.Failure("Parent not found for email $parentEmail"))
            }
        } catch (e: Exception) {
            result(UiState.Failure(e.localizedMessage ?: "An error occurred"))
        }
    }


    override suspend fun getProfilePhoto(
        driver: Driver,
        result: (UiState<Uri?>) -> Unit
    ) {
        try {
            val photoRef = storage.reference.child("photos/${driver.email}")

            val listResult = photoRef.parent?.listAll()?.await()
            val profilePhoto = listResult?.items?.firstOrNull()

            if (profilePhoto == null) {
                result.invoke(UiState.Success(null))
            } else {
                val url = profilePhoto.downloadUrl.await()
                result.invoke(UiState.Success(url))
            }
        } catch (e: Exception) {
            result.invoke(UiState.Failure("Error getting profile photo: ${e.localizedMessage}"))
        }
    }

    override suspend fun getLicencePlateFile(
        driver: Driver,
        result: (UiState<Uri?>) -> Unit
    ) {
        try {
            val fileRef = storage.reference.child("files/${driver.email}")

            val listResult = fileRef.parent?.listAll()?.await()
            val licencePlateFile = listResult?.items?.firstOrNull()

            if (licencePlateFile == null) {
                result.invoke(UiState.Success(null))
            } else {
                val url = licencePlateFile.downloadUrl.await()
                result.invoke(UiState.Success(url))
            }
        } catch (e: Exception) {
            result.invoke(UiState.Failure("Error getting licence plate file: ${e.localizedMessage}"))
        }
    }
}