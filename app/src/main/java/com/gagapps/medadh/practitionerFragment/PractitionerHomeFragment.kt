package com.gagapps.medadh.practitionerFragment

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gagapps.medadh.ProfileDC
import com.gagapps.medadh.R
import com.gagapps.medadh.RetrofitInstance
import com.gagapps.medadh.alarmUtil.ListAlarmHomeAdapter
import com.gagapps.medadh.dataClassPatient.PatientsDC
import com.gagapps.medadh.dataClassPatient.dataClassMultiPatient.Data
import com.gagapps.medadh.dataClassPatient.dataClassMultiPatient.MultiPatientDC
import com.gagapps.medadh.dataClassPractitioner.singlePractitionerDC.SinglePractitionerDC
import com.gagapps.medadh.interfaces.PractitionerServices
import com.gagapps.medadh.practitionerFragment.rvAdapter.ListPatientAdapter
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PractitionerHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PractitionerHomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var profileData: ProfileDC? = null

    private lateinit var listPatientAdapter: ListPatientAdapter
    private val KEY_PROFILE_ID: String = "KEY_PROFILE_ID"
    private val KEY_PROFILE_NAME: String = "KEY_PROFILE_NAME"
    private val KEY_PROFILE_EMAIL: String = "KEY_PROFILE_EMAIL"

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
        return inflater.inflate(R.layout.fragment_practitioner_home, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PractitionerHomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PractitionerHomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvBanner: TextView = view.findViewById(R.id.tv_user_prac_banner)
        tvBanner.text = profileData?.name
        val rvPatient: RecyclerView = view.findViewById(R.id.rv_home_prac)
        getPatientsData(profileData?.id, profileData?.token, rvPatient)
    }

    private fun getPatientsData(pracId: String?, token: String?, rvPatient: RecyclerView){
        var pracData: MultiPatientDC? = null
        val restService: PractitionerServices = RetrofitInstance
            .getRetrofitInstance()
            .create(PractitionerServices::class.java)
        val reqCall = restService.getAllBoundPatients(pracId, "Bearer "+token)
        reqCall.enqueue(object : Callback<MultiPatientDC>{
            override fun onResponse(call: Call<MultiPatientDC>, response: Response<MultiPatientDC>) {
                if(response.message() == "OK"){
                    val data = response.body()
                    if (data!=null){
                        rvPatient.setHasFixedSize(true)
                        listPatientAdapter = ListPatientAdapter(data.data)
                        showRecyclerList(rvPatient)
                    }
                }
                else
                    Log.d("medAdh", "Fail to obtain practitioner data")
            }

            override fun onFailure(call: Call<MultiPatientDC>, t: Throwable) {
                Log.d("medAdh", "<ONFAILURE>Fail to obtain practitioner data")
            }

        })
    }

    private fun loadProfileData(): ProfileDC? {
        var profile : ProfileDC
        try {
            val sharedPreferences: SharedPreferences =
                requireActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
            val gson = Gson()
            val json = sharedPreferences.getString("data Profile", null)
            profile = gson.fromJson(json, ProfileDC::class.java)
            return profile
        } catch (e: Exception){
            e.printStackTrace()
            return null
        }
    }

    private fun showRecyclerList(rvHome: RecyclerView){
        rvHome.layoutManager = LinearLayoutManager(requireContext())
        rvHome.adapter = listPatientAdapter

        listPatientAdapter.setOnItemClickCallback(object : ListPatientAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Data) {
                val detailFrag = PatientDetailFragment()
                val bundle = Bundle()
                bundle.putString(KEY_PROFILE_ID, data._id)
                bundle.putString(KEY_PROFILE_NAME, data.name.given)
                bundle.putString(KEY_PROFILE_EMAIL, data.telecom.value)
                makeCurrentFragment(detailFrag, bundle)
            }

        })
    }

    private fun showSelected(data: Data){
        Toast.makeText(requireContext(), data.name.given+" choosen", Toast.LENGTH_LONG).show()
    }

    private fun makeCurrentFragment(fragment: Fragment, data: Bundle){
        fragment.arguments = data
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.prac_wrapper, fragment)
            commit()
        }
    }


}