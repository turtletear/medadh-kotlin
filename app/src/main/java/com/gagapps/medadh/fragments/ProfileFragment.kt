package com.gagapps.medadh.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.gagapps.medadh.ProfileDC
import com.gagapps.medadh.R
import com.gagapps.medadh.RetrofitInstance
import com.gagapps.medadh.dataClassPractitioner.PractitionerDC
import com.gagapps.medadh.interfaces.PatientsServices
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var profileData: ProfileDC? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        profileData = loadProfileData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val drName: TextView = view.findViewById(R.id.tv_doctor_name)
        val drEmail: TextView = view.findViewById(R.id.tv_doctor_email)
        getDoctorData(drName, drEmail)
        val name = profileData!!.name
        tv_user_profile.text = "${name}'s Profile"


        bt_add_report.setOnClickListener {
            val addReportFrag = ProfileAddReport()
            makeCurrentFragment(addReportFrag)
            Toast.makeText(requireContext(),"To add report detail fragment", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadProfileData(): ProfileDC? {
        var profile : ProfileDC
        try {
            val sharedPreferences: SharedPreferences =
                requireActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
            val gson = Gson()
            val json = sharedPreferences.getString("data Profile", null)
            //val type: Type = object : TypeToken<ProfileDC?>() {}.type
            profile = gson.fromJson(json, ProfileDC::class.java)
            Log.d("medAdh", "load profile success ${profile.name}")
            return profile
        } catch (e: Exception){
            e.printStackTrace()
            return null
        }
    }

    private fun getDoctorData(doctorName: TextView, doctorEmail: TextView){
        val restService: PatientsServices = RetrofitInstance
            .getRetrofitInstance()
            .create(PatientsServices::class.java)
        val reqCall = restService.getDoctorsByPatientId(profileData?.id, "Bearer "+profileData?.token)
        reqCall.enqueue(object : Callback<PractitionerDC>{
            override fun onResponse(call: Call<PractitionerDC>, response: Response<PractitionerDC>) {
                if(response.message() == "OK"){
                    val data = response.body()
                    Log.d("medAdh", "Data : ${data}")
                    if (data != null) {
                        doctorName.text = data.data[0].name.given
                        doctorEmail.text = data.data[0].telecom.value
                    }
                }
                else
                    Log.d("medAdh", "Faile to get Data")
            }

            override fun onFailure(call: Call<PractitionerDC>, t: Throwable) {
                t.printStackTrace()
                Log.d("medAdh", "<ON FAILURE>Faile to get Data")
            }

        })

    }

    private fun makeCurrentFragment(fragment: Fragment){
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.wrapper, fragment)
            commit()
        }
    }
}