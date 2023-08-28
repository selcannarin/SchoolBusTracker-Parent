package com.example.schoolbustrackerparent.ui.student

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolbustrackerparent.data.model.Student
import com.example.schoolbustrackerparent.data.repository.student.StudentRepository
import com.example.schoolbustrackerparent.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val _student = MutableLiveData<UiState<Student>>()
    val student: LiveData<UiState<Student>>
        get() = _student

    private val _updateStudent = MutableLiveData<UiState<Student>>()
    val updateStudent: LiveData<UiState<Student>>
        get() = _updateStudent

    fun getStudent(parentEmail: String) = viewModelScope.launch {
        _student.value = UiState.Loading
        studentRepository.getStudent(parentEmail) { _student.value = it }
    }

    fun updateStudent(student: Student) = viewModelScope.launch {
        _updateStudent.value = UiState.Loading
        studentRepository.updateStudent(student) { _updateStudent.value = it }
    }

}