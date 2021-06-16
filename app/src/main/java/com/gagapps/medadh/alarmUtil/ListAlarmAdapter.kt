package com.gagapps.medadh.alarmUtil

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.gagapps.medadh.R

class ListAlarmAdapter(private val listAlarm: ArrayList<AlarmData>): RecyclerView.Adapter<ListAlarmAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback
    interface OnItemClickCallback {
        fun onItemClicked(data: AlarmData, index: Int)
    }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvMedName: TextView = itemView.findViewById(R.id.tv_med_name)
        var tvClock: TextView = itemView.findViewById(R.id.tv_clock)
        var tvDose: TextView = itemView.findViewById(R.id.tv_dosage)
        var btDel: Button = itemView.findViewById(R.id.bt_del_alrm)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_alarm, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = listAlarm[position]

        holder.tvMedName.text = data.medication
        holder.tvClock.text = showClock(data.hour, data.minute)
        holder.tvDose.text = data.dose.toString()+" "+ data.unit

        holder.btDel.setOnClickListener {
            onItemClickCallback.onItemClicked(listAlarm[holder.adapterPosition], position)
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