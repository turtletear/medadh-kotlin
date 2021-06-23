package com.gagapps.medadh.interfaces

import com.gagapps.medadh.dataClassPatient.LoginFormDC
import com.gagapps.medadh.dataClassPatient.dataClassMultiPatient.MultiPatientDC
import com.gagapps.medadh.dataClassPractitioner.singlePractitionerDC.SinglePractitionerDC
import retrofit2.Call
import retrofit2.http.*

interface PractitionerServices {
    @POST("/auth/login/doctors")
    fun PractitionerLogin(@Body loginForm: LoginFormDC): Call<SinglePractitionerDC>

    @GET("/doctors/{doctorId}")
    fun getPractitionerData(@Path("doctorId") doctorId: String?, @Header("Authorization") token: String?): Call<SinglePractitionerDC>

    @GET("/doctors/patients/bound/{doctorId}")
    fun getAllBoundPatients(@Path("doctorId") doctorId: String?, @Header("Authorization") token: String?): Call<MultiPatientDC>
}