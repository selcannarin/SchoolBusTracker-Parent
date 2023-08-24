package com.example.schoolbustrackerparent.data.repository.student

import com.example.schoolbustrackerparent.data.datasource.student.StudentDataSource
import com.example.schoolbustrackerparent.data.model.Student
import com.example.schoolbustrackerparent.util.UiState
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val dataSource: StudentDataSource
) : StudentRepository {
    override suspend fun getStudent(studentNumber: Int, result: (UiState<Student>) -> Unit) {
        return dataSource.getStudent(studentNumber, result)
    }

    override suspend fun updateStudent(student: Student, result: (UiState<Student>) -> Unit) {
        return dataSource.updateStudent(student, result)
    }
}