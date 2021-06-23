package com.gagapps.medadh.dataClassPractitioner.singlePractitionerDC

import kotlinx.android.parcel.Parcelize

data class SinglePractitionerDC(
    val `data`: Data,
    val status: String,
    val token: String
)