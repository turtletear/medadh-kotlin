package com.gagapps.medadh.dataClassPractitioner.singlePractitionerDC

data class Extension(
    val password: String,
    val patients: List<String>,
    val requestRecieved: List<Any>,
    val username: String
)