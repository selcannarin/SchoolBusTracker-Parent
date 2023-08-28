package com.example.schoolbustrackerparent.ui.student

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.schoolbustrackerparent.R
import com.example.schoolbustrackerparent.data.model.Student
import com.example.schoolbustrackerparent.databinding.FragmentStudentBinding
import com.example.schoolbustrackerparent.ui.MainActivity
import com.example.schoolbustrackerparent.util.UiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StudentFragment : Fragment() {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private var _binding: FragmentStudentBinding? = null
    private val binding get() = _binding
    private val studentViewModel: StudentViewModel by activityViewModels()

    private val TAG = "StudentFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStudentBinding.bind(view)
        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.title = "Student Info"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudentBinding.inflate(inflater, container, false)
        (activity as MainActivity).setBottomNavVisibilityVisible()

        getStudentDetails()
        initListener()

        return binding?.root
    }

    private fun getStudentDetails() {
        val parentEmail = firebaseAuth.currentUser?.email

        if (parentEmail != null) {
            studentViewModel.getStudent(parentEmail)
            studentViewModel.student.observe(viewLifecycleOwner) { studentState ->
                when (studentState) {
                    is UiState.Success -> {
                        val studentInfo = studentState.data
                        binding?.apply {
                            textViewStudentName.text = studentInfo.student_name
                            textViewParentNumber.text = studentInfo.parent_phone_number.toString()
                            textViewStudentNumber.text = studentInfo.student_number.toString()
                            textViewStudentAddress.text = studentInfo.student_address
                        }
                    }

                    else -> {}
                }
            }

        }
    }

    private fun initListener() {

        binding?.apply {

            imageViewEditStudent.setOnClickListener {

                editTextStudentName.setText(textViewStudentName.text)
                textViewStudentName.visibility = View.GONE
                editTextStudentName.visibility = View.VISIBLE

                editTextParentNumber.setText(textViewParentNumber.text)
                textViewParentNumber.visibility = View.GONE
                editTextParentNumber.visibility = View.VISIBLE

                editTextStudentAddress.setText(textViewStudentAddress.text)
                textViewStudentAddress.visibility = View.GONE
                editTextStudentAddress.visibility = View.VISIBLE

                imageViewEditStudent.visibility = View.GONE
                imageViewSaveStudent.visibility = View.VISIBLE

            }
            imageViewSaveStudent.setOnClickListener {
                val newName = editTextStudentName.text.toString()
                val number = textViewStudentNumber.text.toString().toInt()
                val newParentNumber = editTextParentNumber.text.toString().toLong()
                val newAddress = editTextStudentAddress.text.toString()

                val updatedStudent = Student(number, newName, newParentNumber, newAddress)

                checkStudentforUpdate(updatedStudent)
            }

        }

    }

    private fun checkStudentforUpdate(student: Student) {

        if (firebaseAuth.currentUser != null) {

            studentViewModel.updateStudent(student)
            studentViewModel.updateStudent.observe(viewLifecycleOwner) { studentState ->
                when (studentState) {
                    is UiState.Success -> {
                        val updatedStudent = studentState.data
                        Toast.makeText(
                            requireContext(),
                            "Student updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.action_studentFragment_self)
                    }

                    is UiState.Failure -> {

                        Log.e(TAG, "Student update failed.")
                    }

                    else -> {}
                }
            }
        }
    }

}