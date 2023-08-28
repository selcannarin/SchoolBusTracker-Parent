package com.example.schoolbustrackerparent.data.datasource.student

import com.example.schoolbustrackerparent.data.model.Student
import com.example.schoolbustrackerparent.util.UiState

interface StudentDataSource {

    suspend fun getStudent(parentEmail: String, result: (UiState<Student>) -> Unit)

    suspend fun updateStudent(student: Student, result: (UiState<Student>) -> Unit)


}