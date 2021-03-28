package com.gagapps.medadh.dataClassPatient

data class Data(
        val __v: Int,
        val _id: String,
        val createdAt: String,
        val extension: Extension,
        val gender: String,
        val generalPractitioner: List<Any>,
        val name: Name,
        val photo: Photo,
        val resourceType: String,
        val telecom: Telecom,
        val updatedAt: String
)