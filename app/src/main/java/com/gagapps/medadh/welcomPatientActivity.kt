package com.gagapps.medadh

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class welcomPatientActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcom_patient)

        val btnLogin: Button = findViewById(R.id.btn_to_login)
        val btnSignup: Button = findViewById(R.id.btn_to_signup)

        btnLogin.setOnClickListener(this)
        btnSignup.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_to_login -> {
                val moveIntent = Intent(this@welcomPatientActivity, loginActivity::class.java)
                startActivity(moveIntent)

            }
            R.id.btn_to_signup -> {
                val moveIntent = Intent(this@welcomPatientActivity, RegisActivity::class.java)
                startActivity(moveIntent)
            }
        }
    }
}