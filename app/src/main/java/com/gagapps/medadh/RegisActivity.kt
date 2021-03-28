package com.gagapps.medadh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import com.google.android.material.textfield.TextInputLayout
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

                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    isEmptyField = true
                    txEmail.error = "invalid email"
                    txEmail.requestFocus()
                }

                if (pass.isEmpty()){
                    isEmptyField = true
                    txPass.error = errMsg
                    txPass.requestFocus()
                }
                if (gender.isEmpty()){
                    isEmptyField = true
                    cbText.error = errMsg
                    comboGender.requestFocus()
                }

                if(!isEmptyField) {
                    Toast.makeText(applicationContext, "Success!", Toast.LENGTH_LONG).show()
                    return
                }
            }
        }//end when
    }//end onCLick fun
}