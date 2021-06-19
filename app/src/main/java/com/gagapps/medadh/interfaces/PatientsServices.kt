package com.gagapps.medadh.interfaces

import com.gagapps.medadh.dataClassPatient.LoginFormDC
import com.gagapps.medadh.dataClassPatient.PatientsDC
import com.gagapps.medadh.dataClassPatient.dataBodyReq.PatientBodyReq
import com.gagapps.medadh.dataClassReport.ReportDC
import retrofit2.Call
import retrofit2.http.*

interface PatientsServices {

    @POST("/auth/login/patients")
    fun Login(@Body loginForm: LoginFormDC): Call<PatientsDC>

    @POST("/auth/regis/patients")
    fun Regis(@Body bodyReq: PatientBodyReq): Call<PatientsDC>

    @GET("/patients/reports/{patientId}")
    fun getAllPatientReport(@Path("patientId") patientId: String?, @Header("Authorization") token: String?): Call<ReportDC>


}