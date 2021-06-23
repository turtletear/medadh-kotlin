package com.gagapps.medadh.dataClassPatient.dataClassMultiPatient

data class Extension(
    val diagnosticReport: List<String>,
    val medicationStatment: List<String>,
    val password: String,
    val requestSent: List<Any>,
    val username: String
)