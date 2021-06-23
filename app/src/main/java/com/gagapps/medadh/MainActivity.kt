package com.gagapps.medadh

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.gagapps.medadh.dataClassPatient.LoginFormDC
import com.gagapps.medadh.dataClassPatient.PatientsDC
import com.gagapps.medadh.dataClassPractitioner.singlePractitionerDC.SinglePractitionerDC
import com.gagapps.medadh.interfaces.PatientsServices
import com.gagapps.medadh.interfaces.PractitionerServices
import com.gagapps.medadh.loadingClass.LoadingDialog
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnToPatient: Button =  findViewById(R.id.button_patient)
        val btnToPractitioner: Button =  findViewById(R.id.button_practitioner)

        btnToPatient.setOnClickListener(this)
        btnToPractitioner.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.button_patient -> {
                loginAndSaveProfile("patient_dummy1", "pasword123")
            }
            R.id.button_practitioner -> {
                pracLoginAndSaveProfile("doctor_dummy1", "pasword123")
            }
        }
    }

    private fun loginAndSaveProfile(username: String, pass: String){
        val result = LoginReq(username,pass)
        val loading = LoadingDialog(this)
        loading.startLoading()
        result.enqueue(object : Callback<PatientsDC> {
            override fun onResponse(call: Call<PatientsDC>, response: Response<PatientsDC>){

                if (response.message()  == "OK"){
                    loading.isDismiss()
                    val data = response.body()
                    val token = data?.token
                    val id = data?.data?._id
                    val uname = data?.data?.extension?.username
                    val name = data?.data?.name?.given
                    val profData = ProfileDC(uname, name, id, token)
                    saveProfileData(profData)
                    Toast.makeText(applicationContext, "Login Success!", Toast.LENGTH_LONG).show()
                    //to patient home screen
                    val moveIntent = Intent(this@MainActivity, PatientDashboardActivity::class.java)
                    startActivity((moveIntent))
                }
                else {
                    loading.isDismiss()
                    val msg = response.message()
                    Toast.makeText(applicationContext, "Login Failed! $msg", Toast.LENGTH_LONG).show()
                }
            }//end fun
            override fun onFailure(call: Call<PatientsDC>, t: Throwable) {
                loading.isDismiss()
                t.printStackTrace()
                Toast.makeText(applicationContext, "Login Failed!", Toast.LENGTH_LONG).show()
            }//end fun
        })
        return
    }

    private fun pracLoginAndSaveProfile(username: String, pass: String){
        val result = PracLoginReq(username, pass)
        val loading = LoadingDialog(this)
        loading.startLoading()
        result.enqueue(object : Callback<SinglePractitionerDC>{
            override fun onResponse(call: Call<SinglePractitionerDC>, response: Response<SinglePractitionerDC>) {
                Log.d("TAG", "response: $response")
                if(response.message() == "OK"){
                    val data = response.body()
                    val token = data?.token
                    val id = data?.data?._id
                    val uname = data?.data?.extension?.username
                    val name = data?.data?.name?.given
                    val profData = ProfileDC(uname, name, id, token)
                    saveProfileData(profData)
                    Toast.makeText(applicationContext, "Login Success!", Toast.LENGTH_LONG).show()
                    val moveIntent = Intent(this@MainActivity, PractitionerDashboardActivity::class.java)

                    startActivity((moveIntent))
                }
                else{
                    loading.isDismiss()
                    val msg = response.message()
                    Toast.makeText(applicationContext, "Login Failed! $msg", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<SinglePractitionerDC>, t: Throwable) {
                loading.isDismiss()
                t.printStackTrace()
                Toast.makeText(applicationContext, "Login Failed!", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun LoginReq(username: String, pass: String): Call<PatientsDC> {
        val loginForm = LoginFormDC(username, pass) //create body object
        val  retService: PatientsServices = RetrofitInstance
            .getRetrofitInstance()
            .create(PatientsServices::class.java)
        return retService.Login(loginForm)
    }

    private fun PracLoginReq(username: String, pass: String): Call<SinglePractitionerDC>{
        val loginForm = LoginFormDC(username, pass)
        val retService : PractitionerServices = RetrofitInstance
            .getRetrofitInstance()
            .create(PractitionerServices::class.java)
        return  retService.PractitionerLogin(loginForm)
    }

    private fun saveProfileData(data: ProfileDC){
        try {
            val sharedPreferences: SharedPreferences =
                this.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val gson = Gson()
            val json = gson.toJson(data)
            editor.putString("data Profile", json)
            editor.commit()
            Log.d("medAdh", "Profile is saved")
        } catch (e: Exception){
            Log.e("medAdh", e.message.toString())
            e.printStackTrace()
        }
    }

}