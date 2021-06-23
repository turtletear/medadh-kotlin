package com.gagapps.medadh.practitionerFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gagapps.medadh.R
import kotlinx.android.synthetic.main.fragment_patient_detail.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PatientDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PatientDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var KEY_PROFILE_ID: String = "KEY_PROFILE_ID"
    private val KEY_PROFILE_NAME: String = "KEY_PROFILE_NAME"
    private val KEY_PROFILE_EMAIL: String = "KEY_PROFILE_EMAIL"

    private var patient_id: String? = null
    private var patient_name: String? = null
    private var patient_email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            patient_id = it.getString(KEY_PROFILE_ID)
            patient_name = it.getString(KEY_PROFILE_NAME)
            patient_email = it.getString(KEY_PROFILE_EMAIL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_detail, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PatientDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PatientDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bt_back.setOnClickListener {
            val homeFragment = PractitionerHomeFragment()
            makeCurrentFragment(homeFragment)
        }

        val txName: TextView = view.findViewById(R.id.tv_detail_patient_name)
        txName.text = patient_name

        val txEmail : TextView = view.findViewById(R.id.tv_detail_patient_email)
        txEmail.text = patient_email
    }

    private fun makeCurrentFragment(fragment: Fragment){
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.prac_wrapper, fragment)
            commit()
        }
    }
}