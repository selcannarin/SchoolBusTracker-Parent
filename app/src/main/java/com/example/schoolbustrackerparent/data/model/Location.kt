package com.example.schoolbustrackerparent.data.model

import com.google.firebase.Timestamp

data class Location(

    val latitude: Double = 0.0,

    val longitude: Double = 0.0,

    val timestamp: Timestamp
) {
    constructor() : this(0.0, 0.0, Timestamp.now())
}