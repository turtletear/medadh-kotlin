package com.gagapps.medadh.fragments

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
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
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Handler
import androidx.core.content.ContextCompat
import com.gagapps.medadh.dataClassMedState.MedStateDC
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter

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
    private var medStatesData: MedStateDC? = null
    private var reportEntries = arrayListOf<Entry>()
    private var medStateEntries = arrayListOf<Entry>()
    val handler = Handler(Looper.getMainLooper())


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
        val pieChart: PieChart = view.findViewById(R.id.pie_chart)

        reportsData = getAllReportReq(profileData?.id, profileData?.token, lineChart)
        medStatesData = getAllMedStateReq(profileData?.id, profileData?.token, pieChart)


    }


    private fun getAllReportReq(patientId: String?, token: String?, lineChart: LineChart): ReportDC? {
        var reportData: ReportDC? = null
        val restService: PatientsServices = RetrofitInstance
            .getRetrofitInstance()
            .create(PatientsServices::class.java)
        val reqCall = restService.getAllPatientReport(patientId, "Bearer "+token)
        reqCall.enqueue(object : Callback<ReportDC>{
            override fun onResponse(call: Call<ReportDC>, response: Response<ReportDC>) {
                if(response.message() == "OK"){
                    val data = response.body()
                    if (data != null) {
                        reportData = data
                        val lineData = initReportEntryArray(reportData!!)
                        lineChart.data = lineData
                        lineChart.invalidate()
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

    private fun initReportEntryArray(report_data: ReportDC): LineData {
        var entryList = arrayListOf<Entry>()
        val reportsList = report_data.data
        var lineData = LineData()
        if (reportsList != null) {
            var itr = 0
            for (i in reportsList){
                itr++
                val bcrablVal = i.extension.bcr_abl.value.toFloat()
                val entryData = Entry(itr.toFloat(), bcrablVal)
                entryList.add(entryData)
            }//end for

            val dataSet: LineDataSet = LineDataSet(entryList, "BCR/ABL")
            dataSet.color = R.color.black
            dataSet.valueTextColor = Color.LTGRAY
            dataSet.setDrawFilled(true)
            lineData = LineData(dataSet)

        }//end if
        return lineData
    }

    private fun getAllMedStateReq(patientId: String?, token: String?, pieChart: PieChart): MedStateDC? {
        var reportData: MedStateDC? = null
        val restService: PatientsServices = RetrofitInstance
            .getRetrofitInstance()
            .create(PatientsServices::class.java)
        val reqCall = restService.getAllPatientMedState(patientId, "Bearer "+token)
        reqCall.enqueue(object : Callback<MedStateDC>{
            override fun onResponse(call: Call<MedStateDC>, response: Response<MedStateDC>) {
                if(response.message() == "OK"){
                    val data = response.body()
                    if (data != null) {
                        val msData = data

                        var pieData = initMedStateEntryArray(msData)
                        val ttlPercent = getTotalMSPercentage(msData)
                        pieData.setValueFormatter(PercentFormatter(pieChart))
                        pieChart.setCenterText(ttlPercent)
                        pieChart.setCenterTextSize(25f)
                        pieChart.data = pieData
                        pieChart.invalidate()
                    }
                }
                else
                    Log.d("medAdh", "Fail to load patient Medication Statement data")
            }

            override fun onFailure(call: Call<MedStateDC>, t: Throwable) {
                t.printStackTrace()
                Log.d("medAdh", "<ON FAILURE> Fail to load patient Medication Statement data")
            }

        })
        return reportData
    }

    private fun initMedStateEntryArray(medState_data: MedStateDC): PieData {
        var pieEntry = arrayListOf<PieEntry>()
        val medStatesList = medState_data.data
        var colorList = arrayListOf<Int>()
        colorList.add(ContextCompat.getColor(requireContext(), R.color.light_green))
        colorList.add(ContextCompat.getColor(requireContext(), R.color.light_grey))
        var data = PieData()
        var listOfZero = arrayListOf<Float>()
        var listOfOne = arrayListOf<Float>()
        var totalSize = medStatesList?.size
        if (medStatesList != null) {
            var itr = 0
            for (i in medStatesList){
                itr++
                val adhrncBool = i.extension.isLate
                if (adhrncBool)
                    listOfOne.add(1F)
                else
                    listOfZero.add(0F)
            }//end for
            val goodAdh = listOfZero.size.toFloat() / totalSize.toFloat() * 100
            val badAdh = listOfOne.size.toFloat() / totalSize.toFloat() * 100
            Log.d("medAdh", "Good percentage = ${String.format("%.1f", goodAdh)}")
            Log.d("medAdh", "Bad percentage  = ${String.format("%.1f", badAdh)}")
            pieEntry.add(PieEntry(goodAdh, ""))
            pieEntry.add(PieEntry(badAdh, ""))

            val dataSet = PieDataSet(pieEntry, "Adherence percentage")
            dataSet.colors = colorList
            data = PieData(dataSet)
            data.setValueTextSize(0f)
        }//end if

        return data
    }

    private fun getTotalMSPercentage(medState_data: MedStateDC): String {
        var totalPercent : String = "%"
        var goodAdh: Float
        val medStatesList = medState_data.data
        var listOfZero = arrayListOf<Float>()
        var listOfOne = arrayListOf<Float>()
        var totalSize = medStatesList?.size
        if (medStatesList != null) {
            var itr = 0
            for (i in medStatesList){
                itr++
                val adhrncBool = i.extension.isLate
                if (adhrncBool)
                    listOfOne.add(1F)
                else
                    listOfZero.add(0F)
            }//end for
        }//end if
        goodAdh = listOfOne.size.toFloat() / totalSize.toFloat() * 100
        return String.format("%.0f", goodAdh) + "%"
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


}