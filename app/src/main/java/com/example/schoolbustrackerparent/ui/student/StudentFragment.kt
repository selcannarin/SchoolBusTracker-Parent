package com.example.schoolbustrackerparent.ui.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.schoolbustrackerparent.databinding.FragmentStudentBinding
import com.example.schoolbustrackerparent.ui.MainActivity

class StudentFragment : Fragment() {

    private var _binding: FragmentStudentBinding? = null
    private val binding get() = _binding

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
        return binding?.root
    }


}