package com.gagapps.medadh.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.util.Pair
import com.gagapps.medadh.ProfileDC
import com.gagapps.medadh.R
import com.gagapps.medadh.RetrofitInstance
import com.gagapps.medadh.dataClassReport.ReportDC
import com.gagapps.medadh.dataClassReport.bodyRequest.*
import com.gagapps.medadh.dataClassReport.responseDataClassReport.ReportDCforResponse
import com.gagapps.medadh.interfaces.PatientsServices
import com.gagapps.medadh.loadingClass.LoadingDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_profile_add_report.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileAddReport.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileAddReport : Fragment() {
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
        return inflater.inflate(R.layout.fragment_profile_add_report, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileAddReport.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileAddReport().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var dateNow: String = ""
        var dateStart: String = ""
        var dateEnd: String = ""


        val datePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select date")
                .setSelection( Pair(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds())
                )
                .build()

        datePicker.addOnPositiveButtonClickListener {
            val dateSelected = it
            val sdfToSave= SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val sdfToShow = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            dateStart = sdfToSave.format(dateSelected.first)
            dateEnd = sdfToSave.format(dateSelected.second)
            val cal = Calendar.getInstance()
            dateNow = sdfToSave.format(cal.timeInMillis)

            val formatedDateStart = sdfToShow.format(dateSelected.first)
            val formatedDateEnd = sdfToShow.format(dateSelected.second)
            val textShow = formatedDateStart + "  -  " + formatedDateEnd
            tv_date_issued_text.text = textShow.toEditable()

        }

        bt_save_report.setOnClickListener {
            val bcrabl_val = tv_bcrabl_text.text.toString().toInt()
            val wbcell_val = tv_wblood_text.text.toString().toInt()
            val hemoglobin_val = tv_hemoglobin_text.text.toString().toInt()
            val tromb_val = tv_trombosit_text.text.toString().toInt()
            val hema_val = tv_hematokrit_text.text.toString().toInt()
            val eri_val = tv_eritrosit_text.text.toString().toInt()
            val note_val = tv_report_note_text.text.toString()

            val subject = profileData?.id
            val media = Media("www.medAdh.com")
            val effPeriod = EffectivePeriod(dateEnd, dateStart)
            val effective =  Effective(dateNow,effPeriod)
            val bcrabl = BcrAbl(bcrabl_val)
            val hemog = Hemoglobin(hemoglobin_val)
            val wblood = WbloodCell(wbcell_val)
            val trombosit = Trombosit(tromb_val)
            val hematokrit = Hematokrit(hema_val)
            val eritrosit = Eritrosit(eri_val)
            val ext = Extension(bcrabl, hemog, note_val, wblood, trombosit, hematokrit, eritrosit)

            val bodyReq = subject?.let { it1 -> ReportBodyReq(effective, ext, media, it1) }
            addReportReq(bodyReq, profileData?.token)
        }

        tv_date_issued_text.setOnClickListener {
            fragmentManager?.let { it1 -> datePicker.show(it1, "date report issued") }
        }

        bt_back.setOnClickListener {
            val profileFragment= ProfileFragment()
            makeCurrentFragment(profileFragment)
        }

    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun makeCurrentFragment(fragment: Fragment){
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.wrapper, fragment)
            commit()
        }
    }

    private fun addReportReq(data: ReportBodyReq?, token: String?){
        val restService: PatientsServices = RetrofitInstance
            .getRetrofitInstance()
            .create(PatientsServices::class.java)
        val reqCall = restService.createReport(data, "Bearer "+token)
        val loadingDialog = LoadingDialog(mActivity = requireActivity())
        loadingDialog.startLoading()
        reqCall.enqueue(object : Callback<ReportDCforResponse> {


            override fun onResponse(call: Call<ReportDCforResponse>, response: Response<ReportDCforResponse>) {
                if (response.message() == "OK"){
                    loadingDialog.isDismiss()
                    Toast.makeText(requireContext(), "Report Saved", Toast.LENGTH_LONG).show()
                    Log.d("medAdh", "Request status: ${response.body()?.status}")
                }
                else{
                    loadingDialog.isDismiss()
                    Toast.makeText(requireContext(), "Save report failed", Toast.LENGTH_LONG).show()
                    Log.d("medAdh", "Fail to save patient report data, status: ${response.body()?.status}")

                }
            }

            override fun onFailure(call: Call<ReportDCforResponse>, t: Throwable) {
                t.printStackTrace()
                loadingDialog.isDismiss()
                Toast.makeText(requireContext(), "Save report failed", Toast.LENGTH_LONG).show()
                Log.d("medAdh", "<ON FAILURE> Fail to save patient report data")
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