package com.example.schoolbustrackerparent.ui.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolbustrackerparent.data.model.Location
import com.example.schoolbustrackerparent.data.repository.location.LocationRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class BusLocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _getLatestBusLocation = MutableLiveData<Location?>()
    val getLatestBusLocation: LiveData<Location?>
        get() = _getLatestBusLocation

    private val _getStudentAddressByNumber = MutableLiveData<String?>()
    val getStudentAddressByNumber: LiveData<String?>
        get() = _getStudentAddressByNumber

    fun getLatestBusLocation(driverEmail: String) = viewModelScope.launch {
        locationRepository.getLatestBusLocation(driverEmail) { _getLatestBusLocation.value = it }
    }

    fun getStudentAddressByNumber(studentNumber: Int) = viewModelScope.launch {
        locationRepository.getStudentAddressByNumber(studentNumber) {
            _getStudentAddressByNumber.value = it
        }
    }

    fun getLocationFromAddress(context: Context, address: String): LatLng? {
        val geocoder = Geocoder(context)
        val addressList: List<Address>?
        var latLng: LatLng? = null

        try {
            addressList = geocoder.getFromLocationName(address, 1)
            if (addressList != null && addressList.isNotEmpty()) {
                val location = addressList[0]
                val latitude = location.latitude
                val longitude = location.longitude
                latLng = LatLng(latitude, longitude)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return latLng
    }
}