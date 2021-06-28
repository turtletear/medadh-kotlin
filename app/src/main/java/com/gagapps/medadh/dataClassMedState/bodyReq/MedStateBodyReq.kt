package com.gagapps.medadh.dataClassMedState.bodyReq

data class MedStateBodyReq(
    val category: Category,
    val dateAsserted: String,
    val dosage: Dosage,
    val effective: Effective,
    val extension: Extension,
    val note: Note,
    val status: Status,
    val subject: String
)