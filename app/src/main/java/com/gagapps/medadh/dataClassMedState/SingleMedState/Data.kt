package com.gagapps.medadh.dataClassMedState.SingleMedState

data class Data(
    val __v: Int,
    val _id: String,
    val category: Category,
    val createdAt: String,
    val dateAsserted: String,
    val dosage: Dosage,
    val effective: Effective,
    val extension: Extension,
    val note: Note,
    val resourceType: String,
    val status: Status,
    val subject: String,
    val updatedAt: String
)