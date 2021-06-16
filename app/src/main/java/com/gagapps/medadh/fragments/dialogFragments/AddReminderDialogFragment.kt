package com.gagapps.medadh.fragments.dialogFragments

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.gagapps.medadh.R
import com.gagapps.medadh.alarmUtil.AlarmData
import com.gagapps.medadh.alarmUtil.AlarmLogic
import com.gagapps.medadh.fragments.AddReminderFragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_add_reminder_dialog.*
import java.lang.reflect.Type

lateinit var textClock : TextView
lateinit var textMed : TextView
lateinit var textDos : TextView
lateinit var textUnit : TextView
lateinit var textNote : TextView
lateinit var picker : MaterialTimePicker



class AddReminderDialogFragment: DialogFragment() {

    private lateinit var saveButtonListener:  OnSaveButtonListener
    interface OnSaveButtonListener {
        fun onSavePress()
    }
    fun doOnSavePress(){
        saveButtonListener.onSavePress()
    }

    private lateinit var cancelButtonListener:  OnCancelButtonListener
    interface OnCancelButtonListener {
        fun onCancelPress()
    }
    fun doOnCancelPress(){
        cancelButtonListener.onCancelPress()
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var myInflater = inflater.inflate(R.layout.fragment_add_reminder_dialog, container, false)
        return myInflater
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textClock = view.findViewById(R.id.tvClock) as TextView
        textMed = view.findViewById(R.id.med_menu_drop) as TextView
        textDos = view.findViewById(R.id.tfDosageText) as TextView
        textUnit = view.findViewById(R.id.unit_menu_drop) as TextView
        textNote = view.findViewById(R.id.tfNoteText) as TextView

        picker =
                MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(0)
                        .setMinute(0)
                        .setTitleText("Set Reminder")
                        .build()

        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val minute = picker.minute
            val clockShow = showClock(hour, minute)
            textClock.text = clockShow
        }

        btSetTime.setOnClickListener {
            fragmentManager?.let { it1 -> picker.show(it1, "reminder") }
        }


        btnCancel.setOnClickListener {
            doOnCancelPress()
            clearForm(textMed, textDos, textUnit, textNote)
        }

        btnSave.setOnClickListener {
            val hour = picker.hour
            val minute = picker.minute
            val dose = textDos.text.toString().toInt()
            val unit = textUnit.text.toString()
            val medication = textMed.text.toString()
            val note = textNote.text.toString()

            //add alarm logic
            //save alarm data to local storage
//            val size = alarmList.size
//            var alarmData : AlarmData
//            if (size == 0){
//                alarmData = AlarmData(hour, minute, dose, unit, medication, note,0)
//            }
//            else{
//                alarmData = AlarmData(hour, minute, dose, unit, medication, note,size)
//            }


            //dismiss fragment
            doOnSavePress()
            clearForm(textMed, textDos, textUnit, textNote)

        }
    }

    override fun onResume() {
        super.onResume()

        val medication_items = listOf("Imatinib", "Nilotinib", "Dasatinib", "Ponatinib")
        val medication_adapter = ArrayAdapter(requireContext(), R.layout.dropdown_list_item, medication_items)
        (med_menu.editText as? AutoCompleteTextView)?.setAdapter(medication_adapter)

        val unit_items = listOf("Tablets", "Injections", "Miligrams")
        val unit_adapter = ArrayAdapter(requireContext(), R.layout.dropdown_list_item, unit_items)
        (unit_menu.editText as? AutoCompleteTextView)?.setAdapter(unit_adapter)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent is AddReminderFragment){
            val addReminderFragment = parent
            this.cancelButtonListener = addReminderFragment.cancelButtonListener
            this.saveButtonListener = addReminderFragment.saveButtonListener
        }
    }

    private fun clearForm(med: TextView, dos: TextView, unit: TextView, note: TextView){
        med.text = ""
        dos.text = ""
        unit.text = ""
        note.text = ""
    }

    private fun showClock(hour: Int,minute: Int): String {
        var h = hour.toString()
        var m = minute.toString()

        if(hour <= 10)
            h = "0"+h
        if (minute <= 10)
            m = "0"+m

        return "${h}:${m}"
    }

}