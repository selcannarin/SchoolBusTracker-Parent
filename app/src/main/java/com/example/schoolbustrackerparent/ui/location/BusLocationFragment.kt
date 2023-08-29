package com.example.schoolbustrackerparent.ui.location

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.schoolbustrackerparent.R
import com.example.schoolbustrackerparent.databinding.FragmentDriverLocationBinding
import com.example.schoolbustrackerparent.ui.MainActivity
import com.example.schoolbustrackerparent.ui.driver.DriverViewModel
import com.example.schoolbustrackerparent.ui.student.StudentViewModel
import com.example.schoolbustrackerparent.util.UiState
import com.example.schoolbustrackerparent.util.loadUrl
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import javax.inject.Inject

@AndroidEntryPoint
class BusLocationFragment : Fragment(), OnMapReadyCallback {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private var _binding: FragmentDriverLocationBinding? = null
    private val binding get() = _binding
    private val TAG = "BusLocationFragment"

    private val driverViewModel: DriverViewModel by activityViewModels()
    private val locationViewModel: BusLocationViewModel by activityViewModels()
    private val studentViewModel: StudentViewModel by activityViewModels()

    private lateinit var googleMap: GoogleMap

    //private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val locationUpdateInterval = 30000L
    private lateinit var locationTimer: Timer
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDriverLocationBinding.bind(view)
        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.title = "Bus Location"

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        //  locationTimer = Timer()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDriverLocationBinding.inflate(inflater, container, false)
        (activity as MainActivity).setBottomNavVisibilityVisible()
        setupDriverInfo()
        getStudentAddress()
        return binding?.root
    }

    private fun setupDriverInfo() {
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
                            driverViewModel.getProfilePhoto(driverInfo)
                            driverViewModel.getProfilePhoto.observe(viewLifecycleOwner) { photoState ->
                                when (photoState) {
                                    is UiState.Success -> {
                                        val photoUri = photoState.data
                                        imageViewDriverProfile.loadUrl(photoUri.toString())

                                    }

                                    is UiState.Loading -> {

                                    }

                                    is UiState.Failure -> {
                                        Log.e(TAG, "Failed to get driver profile photo.")
                                    }
                                }
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

        binding?.imageViewDriverDetail?.setOnClickListener {
            findNavController().navigate(R.id.action_busLocationFragment_to_driverFragment)
        }
    }

    private fun getStudentAddress() {
        val parentEmail = firebaseAuth.currentUser?.email
        if (parentEmail != null) {
            studentViewModel.getStudent(parentEmail)
            studentViewModel.student.observe(viewLifecycleOwner) { studentState ->

                when (studentState) {
                    is UiState.Success -> {
                        val studentInfo = studentState.data
                        val studentAddressLocation = locationViewModel.getLocationFromAddress(
                            requireContext(),
                            studentInfo.student_address
                        )
                        showStudentAddressOnMap(studentAddressLocation)
                    }

                    else -> {}
                }
            }
        }

    }

    private fun showStudentAddressOnMap(location: LatLng?) {
        val markerOptions = location?.let {
            MarkerOptions().icon(
                BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_student_icon)
            ).position(it).title("Student Address")
        }
        markerOptions?.let { googleMap.addMarker(it) }
        location?.let { googleMap.moveCamera(CameraUpdateFactory.newLatLng(it)) }
        location?.let { CameraUpdateFactory.newLatLngZoom(it, 15f) }
            ?.let { googleMap.animateCamera(it) }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

    }

}