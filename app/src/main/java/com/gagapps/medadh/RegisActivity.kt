package com.gagapps.medadh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import com.gagapps.medadh.dataClassPatient.DataPatient
import com.gagapps.medadh.dataClassPatient.NamePatient
import com.gagapps.medadh.dataClassPatient.PatientsDC
import com.gagapps.medadh.dataClassPatient.TelecomPatient
import com.gagapps.medadh.dataClassPatient.dataBodyReq.ExtensionPatientBodyReq
import com.gagapps.medadh.dataClassPatient.dataBodyReq.NamePatientBodyReq
import com.gagapps.medadh.dataClassPatient.dataBodyReq.PatientBodyReq
import com.gagapps.medadh.dataClassPatient.dataBodyReq.TelecomPatientBodyReq
import com.gagapps.medadh.interfaces.PatientsServices
import com.gagapps.medadh.loadingClass.LoadingDialog
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class RegisActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var comboGender: TextInputLayout
    lateinit var cbText: AutoCompleteTextView
    lateinit var txName: EditText
    lateinit var txUsername: EditText
    lateinit var txEmail: EditText
    lateinit var txPass:EditText
    lateinit var btReg: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regis)

        comboGender = findViewById(R.id.cbGenderREG)
        //add item to combobox
        val items = listOf("Male", "Female", "Unknown", "Other")
        val adapter = ArrayAdapter(this, R.layout.list_item_combobox, items)
        (comboGender.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        txName = findViewById(R.id.txtNameREG)
        txUsername = findViewById(R.id.txtUsernameREG)
        txEmail = findViewById(R.id.txtEmailREG)
        txPass = findViewById(R.id.txtPasswordREG)
        btReg = findViewById(R.id.btnRegis)
        cbText = findViewById(R.id.cbGenderText)

        btReg.setOnClickListener(this)



    }

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.btnRegis -> {
                val name = txName.text.toString()
                val uname = txUsername.text.toString()
                val email =txEmail.text.toString()
                val pass = txPass.text.toString()
                val gender = cbText.text.toString()
                cbText.setError(null)

                val errMsg = "This field can not be empty"
                var isEmptyField = false

                if (name.isEmpty()){
                    isEmptyField = true
                    txName.error = errMsg
                    txName.requestFocus()

                }
                if (uname.isEmpty()){
                    isEmptyField = true
                    txUsername.error = errMsg
                    txUsername.requestFocus()

                }
                if (email.isEmpty()){
                    isEmptyField = true
                    txEmail.error = errMsg
                    txEmail.requestFocus()

                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    isEmptyField = true
                    txEmail.error = "invalid email"
                    txEmail.requestFocus()

                }

                if (pass.isEmpty()){
                    isEmptyField = true
                    txPass.error = errMsg
                    txPass.requestFocus()

                }
                if (gender.isBlank()){
                    isEmptyField = true
                    cbText.error = errMsg
                    comboGender.requestFocus()

                }

                if(!isEmptyField) {
                    val result = RegisReq(name, uname, email, pass, gender)
                    val loading = LoadingDialog(this)
                    loading.startLoading()
                    result.enqueue(object : Callback<PatientsDC>{
                        override fun onResponse(call: Call<PatientsDC>, response: Response<PatientsDC>) {
                            Log.d("TAG", "response: $response")
                            if (response.message() == "OK"){
                                loading.isDismiss()
                                val data = response.body()
                                Toast.makeText(this@RegisActivity, "Regis Success", Toast.LENGTH_LONG).show()
                                //to patient welcom screen
                            }
                            else {
                                loading.isDismiss()
                                val msg = response.message()
                                Toast.makeText(this@RegisActivity, "Regis Failed! $msg", Toast.LENGTH_LONG).show()

                            }
                        }

                        override fun onFailure(call: Call<PatientsDC>, t: Throwable) {
                            loading.isDismiss()
                            Toast.makeText(this@RegisActivity, "Login Failed!", Toast.LENGTH_LONG).show()
                        }
                    })
//                    txName.text.clear()
//                    txEmail.text.clear()
//                    txUsername.text.clear()
//                    txPass.text.clear()
                    return
                }
            }
        }//end when
    }//end onCLick fun

    private fun RegisReq(name: String, uname: String, email: String, pass: String, gender: String): Call<PatientsDC> {
        val nameBodyReq = NamePatientBodyReq(name)
        val teleBodyReq = TelecomPatientBodyReq(email)
        val extBodyReq = ExtensionPatientBodyReq(password = pass, username = uname)
        val bodyReq = PatientBodyReq(extension = extBodyReq, name =  nameBodyReq, telecom = teleBodyReq, gender = gender)

        val retService: PatientsServices = RetrofitInstance
                .getRetrofitInstance()
                .create(PatientsServices::class.java)
        return retService.Regis(bodyReq)


    }

}