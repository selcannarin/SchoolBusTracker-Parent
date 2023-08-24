package com.example.schoolbustrackerparent.data.datasource.student

import com.example.schoolbustrackerparent.data.model.Student
import com.example.schoolbustrackerparent.util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StudentDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StudentDataSource {
    override suspend fun getStudent(studentNumber: Int, result: (UiState<Student>) -> Unit) {
        try {
            val documentSnapshot =
                firestore.collection("students").document(studentNumber.toString()).get().await()

            if (documentSnapshot.exists()) {
                val student = documentSnapshot.toObject(Student::class.java)
                if (student != null) {
                    result(UiState.Success(student))
                } else {
                    result(UiState.Failure("Failed to parse student data"))
                }
            } else {
                result(UiState.Failure("Student not found"))
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