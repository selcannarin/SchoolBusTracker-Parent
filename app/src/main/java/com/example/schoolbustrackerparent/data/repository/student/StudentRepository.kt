package com.example.schoolbustrackerparent.data.repository.student

import com.example.schoolbustrackerparent.data.model.Student
import com.example.schoolbustrackerparent.util.UiState

interface StudentRepository {

    suspend fun getStudent(studentNumber: Int, result: (UiState<Student>) -> Unit)

    suspend fun updateStudent(student: Student, result: (UiState<Student>) -> Unit)

}