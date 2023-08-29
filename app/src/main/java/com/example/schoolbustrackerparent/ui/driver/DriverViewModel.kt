package com.example.schoolbustrackerparent.ui.driver

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolbustrackerparent.data.model.Driver
import com.example.schoolbustrackerparent.data.repository.driver.DriverRepository
import com.example.schoolbustrackerparent.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverViewModel @Inject constructor(
    private val driverRepository: DriverRepository
) : ViewModel() {

    private val _driver = MutableLiveData<UiState<Driver>>()
    val driver: LiveData<UiState<Driver>>
        get() = _driver

    private val _getProfilePhoto = MutableLiveData<UiState<Uri?>>()
    val getProfilePhoto: LiveData<UiState<Uri?>>
        get() = _getProfilePhoto

    private val _getLicencePlateFile = MutableLiveData<UiState<Uri?>>()
    val getLicencePlateFile: LiveData<UiState<Uri?>>
        get() = _getLicencePlateFile

    fun getDriver(parentEmail: String) = viewModelScope.launch {
        _driver.value = UiState.Loading
        driverRepository.getDriver(parentEmail) { _driver.value = it }
    }

    fun getProfilePhoto(driver: Driver) = viewModelScope.launch {
        _getProfilePhoto.value = UiState.Loading
        driverRepository.getProfilePhoto(driver) { _getProfilePhoto.value = it }
    }

    fun getLicencePlateFile(driver: Driver) = viewModelScope.launch {
        _getLicencePlateFile.value = UiState.Loading
        driverRepository.getLicencePlateFile(driver) { _getLicencePlateFile.value = it }
    }


}
