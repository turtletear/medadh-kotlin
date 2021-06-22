package com.gagapps.medadh.interfaces

import com.gagapps.medadh.dataClassPatient.LoginFormDC
import com.gagapps.medadh.dataClassPractitioner.singlePractitionerDC.SinglePractitionerDC
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PractitionerServices {
    @POST("/auth/login/doctors")
    fun PractitionerLogin(@Body loginForm: LoginFormDC): Call<SinglePractitionerDC>
}