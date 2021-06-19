package com.gagapps.medadh

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.gagapps.medadh.dataClassPatient.LoginFormDC
import com.gagapps.medadh.dataClassPatient.PatientsDC
import com.gagapps.medadh.interfaces.PatientsServices
import com.gagapps.medadh.loadingClass.LoadingDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class loginActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var btnLogin: Button
    lateinit var txtUsername: EditText
    lateinit var txtPassword: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txtUsername = findViewById(R.id.txtUsername)
        txtPassword = findViewById(R.id.txtPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLogin -> {
                val username = txtUsername.text.toString()
                val pass = txtPassword.text.toString()
                var isEmptyField = false
                val errMsg = "This field can not be empty"

                if (username.isEmpty()) {
                    isEmptyField = true
                    txtUsername.error = errMsg
                    txtUsername.requestFocus()
                }
                if (pass.isEmpty()) {
                    isEmptyField = true
                    txtPassword.error = errMsg
                    txtPassword.requestFocus()
                    return
                }

                if (!isEmptyField) {
                    val result = LoginReq(username,pass)
                    val loading = LoadingDialog(this)
                    loading.startLoading()
                    result.enqueue(object : Callback<PatientsDC>{
                        override fun onResponse(call: Call<PatientsDC>, response: Response<PatientsDC>){
                            Log.d("TAG", "response: $response")
                            if (response.message()  == "OK"){
                                loading.isDismiss()
                                val data = response.body()
                                Toast.makeText(applicationContext, "Login Success!", Toast.LENGTH_LONG).show()
                                val moveIntent = Intent(this@loginActivity, PatientDashboardActivity::class.java)
                                startActivity(moveIntent)
                                //to patient home screen
                            }
                            else {
                                loading.isDismiss()
                                val msg = response.message()
                                Toast.makeText(applicationContext, "Login Failed! $msg", Toast.LENGTH_LONG).show()
                            }
                        }//end fun
                        override fun onFailure(call: Call<PatientsDC>, t: Throwable) {
                            loading.isDismiss()
                            Toast.makeText(applicationContext, "Login Failed!", Toast.LENGTH_LONG).show()
                        }//end fun
                    })

                    txtUsername.text.clear()
                    txtPassword.text.clear()
                    return
                }
            }
        }//end when
    }//end fun

    private fun LoginReq(username: String, pass: String): Call<PatientsDC> {
        val loginForm = LoginFormDC(username, pass) //create body object
        val  retService: PatientsServices = RetrofitInstance
                .getRetrofitInstance()
                .create(PatientsServices::class.java)
        return retService.Login(loginForm)
    }
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean { //touch anywhere to hide keyboard
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

}