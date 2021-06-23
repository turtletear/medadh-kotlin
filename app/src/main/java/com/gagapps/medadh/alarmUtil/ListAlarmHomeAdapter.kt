package com.gagapps.medadh.alarmUtil

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gagapps.medadh.R

class ListAlarmHomeAdapter(private val listAlarm: ArrayList<AlarmData>): RecyclerView.Adapter<ListAlarmHomeAdapter.ListViewHolder>() {

    private lateinit var onItemCheckedCallback: OnItemCheckedCallback
    interface OnItemCheckedCallback {
        fun onItemChecked(alarmData: AlarmData, position: Int, isChecked:Boolean, layout: LinearLayout, stat: TextView)
    }
    fun setOnItemCheckedCallback(onItemClickCallback: OnItemCheckedCallback){
        this.onItemCheckedCallback = onItemClickCallback
    }


    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvClock: TextView = itemView.findViewById(R.id.tv_home_clock)
        var tvMedNam: TextView = itemView.findViewById(R.id.tv_home_med)
        var tvDose:TextView = itemView.findViewById(R.id.tv_home_dosage)
        var tvStat: TextView = itemView.findViewById(R.id.tv_home_status)
        var layoutBox: LinearLayout = itemView.findViewById(R.id.layout_box)
        var cbStat : CheckBox = itemView.findViewById(R.id.cb_take_med)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_alarm_home, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = listAlarm[position]
        var layout = holder.layoutBox
        holder.tvClock.text = showClock(data.hour, data.minute)
        holder.tvMedNam.text = data.medication
        holder.tvDose.text = data.dose.toString() + " " + data.unit
        var stat = holder.tvStat
        stat.text = data.status

        holder.cbStat.setOnCheckedChangeListener{buttonView, isChecked ->
            onItemCheckedCallback.onItemChecked(listAlarm[holder.adapterPosition], position, isChecked, layout, stat)
        }
    }

    override fun getItemCount(): Int {
        return listAlarm.size
    }

    private fun showClock(hour: Int,minute: Int): String {
        var h = hour.toString()
        var m = minute.toString()

        if(hour < 10)
            h = "0"+h
        if (minute < 10)
            m = "0"+m

        return "${h}:${m}"
    }

}