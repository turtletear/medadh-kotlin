package com.gagapps.medadh.fragments.dialogFragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ContentView
import androidx.fragment.app.DialogFragment
import com.gagapps.medadh.Notifications
import com.gagapps.medadh.R
import com.gagapps.medadh.alarmUtil.AlarmData
import com.gagapps.medadh.alarmUtil.AlarmLogic
import com.gagapps.medadh.databinding.FragmentAddReminderDialogBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.fragment_add_reminder_dialog.*
import java.lang.Exception

class AddReminderDialogFragment: DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var myInflater = inflater.inflate(R.layout.fragment_add_reminder_dialog, container, false)
        return myInflater
    }

    lateinit var alarmLogic: AlarmLogic

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var textClock = view.findViewById(R.id.tvClock) as TextView
        var textMed = view.findViewById(R.id.med_menu_drop) as TextView
        var textDos = view.findViewById(R.id.tfDosageText) as TextView
        var textUnit = view.findViewById(R.id.unit_menu_drop) as TextView
        var textNote = view.findViewById(R.id.tfNoteText) as TextView

        this.alarmLogic = AlarmLogic(requireContext())

        val picker =
                MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(0)
                        .setMinute(0)
                        .setTitleText("Set Reminder")
                        .build()

        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val minute = picker.minute
            if (hour>10)
                textClock.text = hour.toString()+ ":"+minute.toString()
            else
                textClock.text = "0"+hour.toString()+ ":0"+minute.toString()
        }

        btSetTime.setOnClickListener {
            fragmentManager?.let { it1 -> picker.show(it1, "reminder") }
        }

        btnCancel.setOnClickListener {
            clearForm(textMed, textDos, textUnit, textNote)
            cancelAlarm()
            dismiss()
        }

        btnSave.setOnClickListener {
            val hour = picker.hour
            val minute = picker.minute
            val dose = textDos.text.toString().toInt()
            val unit = textUnit.text.toString()
            val medication = textMed.text.toString()
            val note = textNote.text.toString()

            val alarmData = AlarmData(hour, minute, dose, unit, medication, note)


            Log.d("Mantap", "Medication: ${medication}, Dosage ${dose}${unit}")
            Log.d("Mantap", "Reminder set. Hour: ${hour}, Minute: ${minute}")
            Log.d("Mantap", "Note : ${note}")

            //add alarm logic
            setAlarm(alarmData)
            //save alarm data to local storage

            //dismiss fragment
            clearForm(textMed, textDos, textUnit, textNote)
            dismiss()

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

    private fun clearForm(med: TextView, dos: TextView, unit: TextView, note: TextView){
        med.text = ""
        dos.text = ""
        unit.text = ""
        note.text = ""
    }

    private fun setAlarm(data: AlarmData){
        alarmLogic.SetAlarm(data.hour, data.minute)
    }

    private fun cancelAlarm(){
        alarmLogic.cancelAlarm()
    }

}