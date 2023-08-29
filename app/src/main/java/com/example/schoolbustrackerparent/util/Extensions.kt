package com.example.schoolbustrackerparent.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.schoolbustrackerparent.R

fun ImageView.loadUrl(url: String) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.driver_profile_icon)
        .circleCrop()
        .into(this)
}