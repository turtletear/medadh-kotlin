package com.gagapps.medadh.interfaces

import com.gagapps.medadh.dataClassMedState.MedStateDC
import com.gagapps.medadh.dataClassPatient.LoginFormDC
import com.gagapps.medadh.dataClassPatient.PatientsDC
import com.gagapps.medadh.dataClassPatient.dataBodyReq.PatientBodyReq
import com.gagapps.medadh.dataClassPractitioner.PractitionerDC
import com.gagapps.medadh.dataClassReport.ReportDC
import com.gagapps.medadh.dataClassReport.bodyRequest.ReportBodyReq
import com.gagapps.medadh.dataClassReport.responseDataClassReport.ReportDCforResponse
import retrofit2.Call
import retrofit2.http.*

interface PatientsServices {

    @POST("/auth/login/patients")
    fun Login(@Body loginForm: LoginFormDC): Call<PatientsDC>

    @POST("/auth/regis/patients")
    fun Regis(@Body bodyReq: PatientBodyReq): Call<PatientsDC>

    @POST("/patients/reports")
    fun createReport(@Body bodyReq: ReportBodyReq?, @Header("Authorization") token: String?): Call<ReportDCforResponse>

    @GET("/patients/reports/{patientId}")
    fun getAllPatientReport(@Path("patientId") patientId: String?, @Header("Authorization") token: String?): Call<ReportDC>

    @GET("/patients/medicationState/{patientId}")
    fun getAllPatientMedState(@Path("patientId")patientId: String?, @Header("Authorization") token: String?): Call<MedStateDC>

    @GET("/patients/doctor/search/{patientId}")
    fun getDoctorsByPatientId(@Path("patientId")patientId: String?, @Header("Authorization") token: String?): Call<PractitionerDC>



}