package com.gagapps.medadh.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gagapps.medadh.R
import com.gagapps.medadh.alarmUtil.AlarmData
import com.gagapps.medadh.alarmUtil.ListAlarmAdapter
import com.gagapps.medadh.alarmUtil.ListAlarmHomeAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_row_alarm_home.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var rvHomeAlarm: RecyclerView
    private var alarmList = arrayListOf<AlarmData>()
    private lateinit var listAlarmHomeAdapter: ListAlarmHomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        alarmList = loadListData()
        listAlarmHomeAdapter = ListAlarmHomeAdapter(alarmList)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                HomeFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tv_home_day.text = LocalDate.now().dayOfWeek.toString()

            val fulldate = LocalDate.now().dayOfMonth.toString()+ " " + LocalDate.now().month + " " + LocalDate.now().year
            tv_home_date.text = fulldate
        }

        rvHomeAlarm = view.findViewById(R.id.rv_home_alarm)
        rvHomeAlarm.setHasFixedSize(true)

        showRecyclerList(alarmList)
    }

    private fun showRecyclerList(dataList: ArrayList<AlarmData>){
        rvHomeAlarm.layoutManager = LinearLayoutManager(requireContext())
        rvHomeAlarm.adapter = listAlarmHomeAdapter

        listAlarmHomeAdapter.setOnItemCheckedCallback(object : ListAlarmHomeAdapter.OnItemCheckedCallback{
            override fun onItemChecked(alarmData: AlarmData, position: Int, isChecked:Boolean) {
                medTakenLogic(alarmData, isChecked)

            }

        })

    }

    private fun medTakenLogic(alarmData: AlarmData, isChecked:Boolean){
        val calendar: Calendar = Calendar.getInstance()
        val simpleDataFormat: SimpleDateFormat = SimpleDateFormat("HH:mm")
        val currentTime = simpleDataFormat.format(calendar.time)


        val calendar2: Calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarmData.hour)
            set(Calendar.MINUTE, alarmData.minute)
        }
        val timeShouldBe = simpleDataFormat.format(calendar2.time)


        val calendarDummy: Calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 2)
            set(Calendar.MINUTE, 30)
        }

        val calendarDummy2: Calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 1)
            set(Calendar.MINUTE, 30)
        }

        Log.d("Mantap", "Current Time: ${currentTime}")
        Log.d("Mantap", "Time Should Be: ${timeShouldBe}")

        val diff = calendarDummy.timeInMillis - calendarDummy2.timeInMillis
        Log.d("Mantap", "Time Diff : ${diff/60000}")

        if(!isChecked){
            layout_box.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
            tv_home_status.text = "Oncoming"
        }
        else {
            val limit: Long = 60
            if (diff <= limit) {
                layout_box.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_green))
                tv_home_status.text = "Taken"
            }
            else{
                layout_box.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.warning_red))
                tv_home_status.text = "Late"
            }

        }
    }

    private fun loadListData(): ArrayList<AlarmData> {
        var loadedList = arrayListOf<AlarmData>()
        try {
            val sharedPreferences: SharedPreferences =
                activity!!.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
            val gson = Gson()
            val json = sharedPreferences.getString("data alarm list", null)
            val type: Type = object : TypeToken<ArrayList<AlarmData?>?>() {}.type
            loadedList = gson.fromJson<Any>(json, type) as ArrayList<AlarmData>

            return loadedList
        } catch (e: Exception){
            e.printStackTrace()
            return loadedList
        }
    }


    private fun saveListData(dataList: ArrayList<AlarmData>){
        try {
            val sharedPreferences: SharedPreferences =
                activity!!.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val gson = Gson()
            val json = gson.toJson(dataList)
            editor.putString("data alarm list", json)
            editor.apply()
            Log.d("Mantap", "the whole list is saved")
        } catch (e: Exception){
            Log.e("Mantap", e.message.toString())
            e.printStackTrace()
        }
    }

}