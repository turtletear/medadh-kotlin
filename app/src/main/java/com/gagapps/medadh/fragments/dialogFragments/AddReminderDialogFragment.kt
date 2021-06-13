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
import com.gagapps.medadh.R
import com.gagapps.medadh.databinding.FragmentAddReminderDialogBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.fragment_add_reminder_dialog.*

class AddReminderDialogFragment: DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_reminder_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val picker =
                MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(0)
                        .setMinute(0)
                        .setTitleText("Set Reminder")
                        .build()

        btSetTime.setOnClickListener {
            fragmentManager?.let { it1 -> picker.show(it1, "reminder") }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnSave.setOnClickListener {
            val hour = picker.hour
            val minute = picker.minute
            val dose = tfDosageText.text
            val unit = unit_menu_drop.text
            val medication = med_menu_drop.text
            val note = tfNoteText.text

            Log.d("Mantao", "Medication: ${medication}, Dosage${dose} ${unit}")
            Log.d("Mantap", "Reminder set. Hour: ${hour}, Minute: ${minute}")
            Log.d("Mantap", "Note : ${note}")
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

}