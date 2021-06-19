package com.gagapps.medadh.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gagapps.medadh.ProfileDC
import com.gagapps.medadh.R
import com.gagapps.medadh.RetrofitInstance
import com.gagapps.medadh.dataClassReport.ReportDC
import com.gagapps.medadh.interfaces.PatientsServices
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.logging.Handler

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdherenceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdherenceFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var profileData: ProfileDC? = null
    private var reportsData: ReportDC? = null
    private var entries = arrayListOf<Entry>()

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
        return inflater.inflate(R.layout.fragment_adherence, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdherenceFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdherenceFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lineChart: LineChart = view.findViewById(R.id.line_chart)
        val token = ""
        reportsData = getAllReportReq(profileData?.id, profileData?.token)
        // TODO: Add handler to wait the response


    }

    private fun getAllReportReq(patientId: String?, token: String?): ReportDC? {
        var reportData: ReportDC? = null
        val restService: PatientsServices = RetrofitInstance
            .getRetrofitInstance()
            .create(PatientsServices::class.java)
        val reqCall = restService.getAllPatientReport(patientId, "Bearer "+token)
        reqCall.enqueue(object : Callback<ReportDC>{
            override fun onResponse(call: Call<ReportDC>, response: Response<ReportDC>) {
                Log.d("medAdh", "response message: ${response.message()}")
                Log.d("medAdh", "response: ${response.body()}")
                if(response.message() == "OK"){
                    val data = response.body()
                    if (data != null) {
                        reportData = data
                    }
                }
                else
                    Log.d("medAdh", "Fail to load patient report data")
            }

            override fun onFailure(call: Call<ReportDC>, t: Throwable) {
                Log.d("medAdh", "<ON FAILURE> Fail to load patient report data")
            }

        })
        return reportData
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
}