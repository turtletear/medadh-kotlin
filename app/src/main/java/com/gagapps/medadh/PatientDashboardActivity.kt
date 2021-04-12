package com.gagapps.medadh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.gagapps.medadh.fragments.AddReminderFragment
import com.gagapps.medadh.fragments.AdherenceFragment
import com.gagapps.medadh.fragments.HomeFragment
import com.gagapps.medadh.fragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_patient_dashboard.*

class PatientDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_dashboard)



        val homeFrag = HomeFragment()
        val adhFrag = AdherenceFragment()
        val reminderFrag = AddReminderFragment()
        val profFrag = ProfileFragment()

        makeCurrentFragment(homeFrag)

        bottom_navigation.setOnNavigationItemSelectedListener{
            when (it.itemId){
                R.id.it_home -> makeCurrentFragment(homeFrag)
                R.id.it_adherence -> makeCurrentFragment(adhFrag)
                R.id.it_reminder -> makeCurrentFragment(reminderFrag)
                R.id.it_profile -> makeCurrentFragment(profFrag)
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