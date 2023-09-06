package com.example.schoolbustrackerparent.ui.driver

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.schoolbustrackerparent.databinding.FragmentDriverBinding
import com.example.schoolbustrackerparent.ui.MainActivity
import com.example.schoolbustrackerparent.util.UiState
import com.example.schoolbustrackerparent.util.loadUrl
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DriverFragment : Fragment() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private var _binding: FragmentDriverBinding? = null
    private val binding get() = _binding
    private val driverViewModel: DriverViewModel by activityViewModels()

    private val TAG = "DriverFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDriverBinding.bind(view)
        (activity as MainActivity).showNavigationDrawer()
        (requireActivity() as MainActivity).setToolbarTitle("Driver Info")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDriverBinding.inflate(inflater, container, false)
        (activity as MainActivity).setBottomNavVisibilityVisible()
        (activity as MainActivity).showNavigationDrawer()
        getDriverInfo()
        return binding?.root
    }

    private fun getDriverInfo() {
        val parentEmail = firebaseAuth.currentUser?.email

        if (parentEmail != null) {
            driverViewModel.getDriver(parentEmail)
            driverViewModel.driver.observe(viewLifecycleOwner) { driver ->

                when (driver) {
                    is UiState.Success -> {
                        val driverInfo = driver.data
                        binding?.apply {
                            textViewDriverName.text = driverInfo.fullName
                            textViewLicensePlate.text = driverInfo.licensePlate
                            textViewDriverEmail.text = driverInfo.email
                            textViewDriverPhone.text = driverInfo.phone.toString()
                            driverViewModel.getProfilePhoto(driverInfo)
                            driverViewModel.getProfilePhoto.observe(viewLifecycleOwner) { photoState ->
                                when (photoState) {
                                    is UiState.Success -> {
                                        val photoUri = photoState.data
                                        imageViewDriverImage.loadUrl(photoUri.toString())

                                    }

                                    is UiState.Loading -> {

                                    }

                                    is UiState.Failure -> {
                                        Log.e(TAG, "Failed to get driver profile photo.")
                                    }
                                }
                            }

                            licensePlateIcon.setOnClickListener {

                                driverViewModel.getLicencePlateFile(driverInfo)
                                driverViewModel.getLicencePlateFile.observe(viewLifecycleOwner) { fileState ->
                                    when (fileState) {
                                        is UiState.Success -> {
                                            val fileUri = fileState.data
                                            val intent = Intent(Intent.ACTION_VIEW)
                                            intent.setDataAndType(
                                                Uri.parse(fileUri.toString()),
                                                "application/pdf"
                                            )
                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            startActivity(intent)

                                        }

                                        is UiState.Loading -> {

                                        }

                                        is UiState.Failure -> {
                                            Log.e(TAG, "Failed to get driver licence plate file.")
                                        }
                                    }
                                }

                            }

                            imageViewDriverPhone.setOnClickListener {
                                dialPhoneNumber(driverInfo.phone.toString())
                            }

                        }
                    }

                    is UiState.Failure -> {
                        Log.e(TAG, "Failed to get driver data.")
                    }

                    is UiState.Loading -> {}
                }
            }
        }
    }

    private fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

}