package com.gagapps.medadh.dataClassPatient

data class DataPatient(
        val __v: Int,
        val _id: String,
        val createdAt: String,
        val extensionPatient: ExtensionPatient,
        val gender: String,
        val generalPractitioner: List<Any>,
        val namePatient: NamePatient,
        val photoPatient: PhotoPatient,
        val resourceType: String,
        val telecomPatient: TelecomPatient,
        val updatedAt: String
)