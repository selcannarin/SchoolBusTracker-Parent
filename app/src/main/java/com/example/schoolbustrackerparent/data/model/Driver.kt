package com.example.schoolbustrackerparent.data.model

import android.net.Uri

data class Driver(

    val email: String = "",

    val fullName: String = "",

    val licensePlate: String = "",

    val licensePlateUri: Uri?,

    val profilePhotoUri: Uri?

)

