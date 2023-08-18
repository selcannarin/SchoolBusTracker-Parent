package com.example.schoolbustrackerparent.data.model

data class Parent(

    val email: String,

    val studentNumber: Int = 0,

    val phoneNumber: Long = 0,

    val studentName: String = "",

    val studentAddress: String = "",

    val driver: Driver

)
