package com.gagapps.medadh.dataClassReport

data class ReportData(
    val __v: Int,
    val _id: String,
    val category: Category,
    val createdAt: String,
    val effective: Effective,
    val extension: Extension,
    val issued: String,
    val media: List<Media>,
    val resourceType: String,
    val status: String,
    val subject: String,
    val updatedAt: String
)