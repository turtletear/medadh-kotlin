package com.gagapps.medadh.dataClassPatient.dataBodyReq

data class PatientBodyReq(
        val extension: ExtensionPatientBodyReq,
        val gender: String,
        val name: NamePatientBodyReq,
        val telecom: TelecomPatientBodyReq
)