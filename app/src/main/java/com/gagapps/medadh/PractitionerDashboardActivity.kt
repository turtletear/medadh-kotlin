package com.gagapps.medadh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.gagapps.medadh.practitionerFragment.PractitionerHomeFragment
import com.gagapps.medadh.practitionerFragment.PractitionerProfileFragment
import kotlinx.android.synthetic.main.activity_practitioner_dashboard.*

class PractitionerDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practitioner_dashboard)

        val homeFrag = PractitionerHomeFragment()
        val profFrag = PractitionerProfileFragment()

        bottom_nav_prac.setOnNavigationItemReselectedListener {
            when(it.itemId){
                R.id.it_prac_home -> makeCurrentFragment(homeFrag)
                R.id.it_prac_profile -> makeCurrentFragment(profFrag)
            }
        }
    }

    private fun makeCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.prac_wrapper, fragment)
            commit()
        }
    }
}