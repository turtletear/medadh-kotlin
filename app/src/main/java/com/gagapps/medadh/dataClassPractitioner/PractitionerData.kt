package com.gagapps.medadh.dataClassPractitioner

data class PractitionerData(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val extension: Extension,
    val gender: String,
    val name: Name,
    val photo: Photo,
    val resourceType: String,
    val telecom: Telecom,
    val updatedAt: String
)