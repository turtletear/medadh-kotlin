package com.gagapps.medadh.interfaces

import com.gagapps.medadh.dataClassPatient.LoginFormDC
import com.gagapps.medadh.dataClassPatient.PatientsDC
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PatientsServices {

    @POST("/auth/login/patients")
    fun Login(@Body loginForm: LoginFormDC): Call<PatientsDC>
}