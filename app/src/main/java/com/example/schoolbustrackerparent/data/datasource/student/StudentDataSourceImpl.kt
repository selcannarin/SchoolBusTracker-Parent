package com.example.schoolbustrackerparent.data.datasource.student

import com.example.schoolbustrackerparent.data.model.Student
import com.example.schoolbustrackerparent.util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StudentDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StudentDataSource {
    override suspend fun getStudent(parentEmail: String, result: (UiState<Student>) -> Unit) {
        try {
            val parentDocument = firestore.collection("parents").document(parentEmail).get().await()

            if (parentDocument.exists()) {
                val studentNumber = parentDocument.getLong("student_number")

                if (studentNumber != null) {
                    val studentDocument =
                        firestore.collection("students").document(studentNumber.toString()).get().await()

                    if (studentDocument.exists()) {
                        val student = studentDocument.toObject(Student::class.java)
                        if (student != null) {
                            result(UiState.Success(student))
                        } else {
                            result(UiState.Failure("Failed to parse student data"))
                        }
                    } else {
                        result(UiState.Failure("Student not found"))
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


    override suspend fun updateStudent(student: Student, result: (UiState<Student>) -> Unit) {
        try {
            val studentNumber = student.student_number

            firestore.collection("students")
                .document(studentNumber.toString())
                .set(student)
                .addOnSuccessListener {
                    result(UiState.Success(student))
                }
                .addOnFailureListener { e ->
                    result(UiState.Failure(e.localizedMessage ?: "Failed to update student"))
                }
        } catch (e: Exception) {
            result(UiState.Failure(e.localizedMessage ?: "An error occurred"))
        }
    }


}