package com.gagapps.medadh.dataClassReport.bodyRequest

data class ReportBodyReq(
    val effective: Effective,
    val extension: Extension,
    val media: Media,
    val subject: String
)