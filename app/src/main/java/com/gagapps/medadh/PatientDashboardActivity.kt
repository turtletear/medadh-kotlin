package com.gagapps.medadh

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.gagapps.medadh.fragments.*
import kotlinx.android.synthetic.main.activity_patient_dashboard.*


class PatientDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_dashboard)



        val homeFrag = HomeFragment()
        val adhFrag = AdherenceFragment()
        val reminderFrag = AddReminderFragment()
        val profFrag = ProfileFragment()
        val blueFrag = BluetoothFragment()
        makeCurrentFragment(homeFrag)

        bottom_navigation.setOnNavigationItemSelectedListener{
            when (it.itemId){
                R.id.it_home -> makeCurrentFragment(homeFrag)
                R.id.it_adherence -> makeCurrentFragment(adhFrag)
                R.id.it_reminder -> makeCurrentFragment(reminderFrag)
                R.id.it_profile -> makeCurrentFragment(profFrag)
                R.id.it_bluetooth -> makeCurrentFragment(blueFrag)
            }
            true
        }


    }

    private fun makeCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.wrapper, fragment)
            commit()
        }
    }

}