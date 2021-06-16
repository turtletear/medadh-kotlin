package com.gagapps.medadh.fragments

import android.app.AlarmManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gagapps.medadh.R
import com.gagapps.medadh.alarmUtil.AlarmData
import com.gagapps.medadh.alarmUtil.AlarmLogic
import com.gagapps.medadh.alarmUtil.ListAlarmAdapter
import com.gagapps.medadh.fragments.dialogFragments.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_add_reminder.*
import java.lang.reflect.Type

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddReminderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddReminderFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var rvAlarm: RecyclerView
    private var alarmLogicList = arrayListOf<AlarmLogic>()

    private var alarmList = arrayListOf<AlarmData>()
    private val dialog = AddReminderDialogFragment()
    private lateinit var listAlarmAdapter: ListAlarmAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        alarmList = loadListData()
        alarmLogicList = resetAndLoadAlarmLogic()
        Log.d("Mantap", "List size: ${alarmList.size}")
        listAlarmAdapter = ListAlarmAdapter(alarmList)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_reminder, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddReminderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddReminderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mFragmentManager = childFragmentManager

        rvAlarm = view.findViewById(R.id.rv_alarm)
        rvAlarm.setHasFixedSize(true)

        showRecyclerList(alarmList)

        add_reminder.setOnClickListener {
                dialog.isCancelable = false
                dialog.show(mFragmentManager, AddReminderDialogFragment::class.java.simpleName)
        }
    }

    private fun showRecyclerList(dataList: ArrayList<AlarmData>){

        rvAlarm.layoutManager = LinearLayoutManager(requireContext())
        rvAlarm.adapter = listAlarmAdapter

        listAlarmAdapter.setOnItemClickCallback(object : ListAlarmAdapter.OnItemClickCallback{
            override fun onItemClicked(data: AlarmData, index: Int) {
                deleteData(index)
                Toast.makeText(requireContext(), "Alarm deleted!", Toast.LENGTH_LONG).show()
            }
        })

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

    private fun resetAndLoadAlarmLogic(): ArrayList<AlarmLogic> {
        var alarmLogicList = arrayListOf<AlarmLogic>()
        if (alarmList.size  != 0 ){
            for (data in alarmList){
                val newAlarmLogic = AlarmLogic(requireContext(), data)
                newAlarmLogic.setAlarm()
                alarmLogicList.add(newAlarmLogic)
            }
        }
        return alarmLogicList
        Log.d("Mantap", "Reset & Reload Alarm Logic Complete")
    }


    private fun deleteData(index: Int){
        val reqCode = alarmList[index].reqCode
        val alrmLogic = findAlarmLogic(reqCode)
        alrmLogic!!.cancelAlarm()
        alarmList.removeAt(index)
        try {
            val sharedPreferences: SharedPreferences =
                activity!!.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val gson = Gson()
            val json = gson.toJson(alarmList)
            editor.putString("data alarm list", json)
            editor.apply()
            Log.d("Mantap", "the whole list is updated")
            listAlarmAdapter.notifyDataSetChanged()
        } catch (e: Exception){
            e.printStackTrace()
        }


    }

    internal val cancelButtonListener: AddReminderDialogFragment.OnCancelButtonListener =object : AddReminderDialogFragment.OnCancelButtonListener{
        override fun onCancelPress() {
            dialog.dismiss()
        }

    }

    internal val saveButtonListener: AddReminderDialogFragment.OnSaveButtonListener = object : AddReminderDialogFragment.OnSaveButtonListener{
        override fun onSavePress() {
            val hour = picker.hour
            val minute = picker.minute
            val dose = textDos.text.toString().toInt()
            val unit = textUnit.text.toString()
            val medication = textMed.text.toString()
            val note = textNote.text.toString()

            val size = alarmList.size
            val reqCode = (0..500).random() + size + hour + minute
            val statDef = "Oncoming"

            if (size == 0){
                val alarmData = AlarmData(hour, minute, dose, unit, medication, note,0, statDef)
                alarmList.add(alarmData)
                addNewAlarm(alarmData)
                listAlarmAdapter.notifyDataSetChanged()
                Log.d("Mantap", "Data saved")
            }
            else{
                val alarmData = AlarmData(hour, minute, dose, unit, medication, note,reqCode, statDef)
                alarmList.add(alarmData)
                addNewAlarm(alarmData)
                listAlarmAdapter.notifyDataSetChanged()
                Log.d("Mantap", "Data saved")
            }
            saveListData(alarmList)
            dialog.dismiss()
        }

    }

    private fun addNewAlarm(data: AlarmData) {
        val alarm : AlarmLogic
        alarm = AlarmLogic(requireContext(), data)
        alarm.setAlarm()
        alarmLogicList.add(alarm)
    }

    private fun findAlarmLogic(reqCode: Int): AlarmLogic? {
        var retDat: AlarmLogic? = null
        for (data in alarmLogicList){
            if (data.reqCode == reqCode){
                retDat = data
            }
        }
        return retDat
    }

}