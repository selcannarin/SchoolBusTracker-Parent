package com.example.schoolbustrackerparent.ui.location

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.schoolbustrackerparent.R
import com.example.schoolbustrackerparent.data.model.Location
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
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject


@AndroidEntryPoint
class BusLocationFragment : Fragment(), OnMapReadyCallback {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private var _binding: FragmentDriverLocationBinding? = null
    private val binding get() = _binding

    private val driverViewModel: DriverViewModel by activityViewModels()
    private val locationViewModel: BusLocationViewModel by activityViewModels()
    private val studentViewModel: StudentViewModel by activityViewModels()

    private val TAG = "BusLocationFragment"
    private lateinit var googleMap: GoogleMap
    private var currentUserMarker: Marker? = null
    private val allMarkers: MutableList<Marker> = mutableListOf()

    private var locationTimer: Timer? = null
    private var locationTimerTask: TimerTask? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDriverLocationBinding.bind(view)
        (requireActivity() as MainActivity).setToolbarTitle("Bus Location")
        (activity as MainActivity).showNavigationDrawer()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDriverLocationBinding.inflate(inflater, container, false)
        (activity as MainActivity).setBottomNavVisibilityVisible()
        (activity as MainActivity).showNavigationDrawer()

        onBackPressed()
        setupDriverInfo()
        getStudentAddress()
        startLocationUpdates()

        return binding?.root
    }

    private fun startLocationUpdates() {
        locationTimer = Timer()
        locationTimerTask = object : TimerTask() {
            override fun run() {
                val parentEmail = firebaseAuth.currentUser?.email

                if (parentEmail != null) {
                    driverViewModel.getDriver(parentEmail)

                    view?.post {
                        driverViewModel.driver.observe(viewLifecycleOwner) { driver ->
                            when (driver) {
                                is UiState.Success -> {
                                    val driverInfo = driver.data
                                    if (driverInfo.email != null) {
                                        getLatestBusLocation(driverInfo.email)
                                    }
                                }

                                is UiState.Loading -> {
                                    Log.d(TAG, "Loading driver data.")
                                }

                                else -> {
                                    Log.e(TAG, "Failed to get driver data.")
                                }
                            }
                        }
                    }
                }
            }
        }

        locationTimer?.schedule(
            locationTimerTask,
            0,
            30000
        )
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
                        val studentAddressLocation =
                            locationViewModel.getLocationFromAddress(
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

        val studentHouseMarker = location?.let {
            MarkerOptions()
                .position(it)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_student_icon))
                .title("Student Address")
        }?.let {
            googleMap.addMarker(
                it
            )
        }
        studentHouseMarker?.let { allMarkers.add(it) }
        showAllMarkers()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }

    private fun showAllMarkers() {
        val builder = LatLngBounds.Builder()
        for (marker in allMarkers) {
            builder.include(marker.position)
        }
        val bounds = builder.build()
        val padding = 150
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        googleMap.moveCamera(cameraUpdate)
    }

    private fun getLatestBusLocation(driverEmail: String) {
        locationViewModel.getLatestBusLocation(driverEmail)
        locationViewModel.getLatestBusLocation.observe(viewLifecycleOwner) { location ->
            if (location != null) {
                updateMapWithLocation(location)
            }
        }
    }

    private fun updateMapWithLocation(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        val busLocation = LatLng(latitude, longitude)
        currentUserMarker?.remove()
        currentUserMarker = googleMap.addMarker(
            MarkerOptions()
                .position(busLocation)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_driver_icon))
                .title("Driver Location")
        )
        currentUserMarker?.let { allMarkers.add(it) }
        showAllMarkers()
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationTimer?.cancel()
        locationTimerTask?.cancel()
    }

}