package com.gagapps.medadh

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

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
                val moveIntent = Intent(this@MainActivity, welcomPatientActivity::class.java)
                startActivity((moveIntent))
            }
            R.id.button_practitioner -> {
                Toast.makeText(applicationContext, "TO PRACTITIONER SECTION", Toast.LENGTH_LONG).show()
            }
        }
    }
}