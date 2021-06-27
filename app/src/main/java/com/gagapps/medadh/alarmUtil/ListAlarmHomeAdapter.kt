package com.gagapps.medadh.alarmUtil

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.gagapps.medadh.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ListAlarmHomeAdapter(private val listAlarm: ArrayList<AlarmData>, val lifecycleOwner: LifecycleOwner): RecyclerView.Adapter<ListAlarmHomeAdapter.ListViewHolder>() {

    private lateinit var onItemCheckedCallback: OnItemCheckedCallback
    private var liveData = MutableLiveData<String>()

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
        var ctx = itemView.context
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_alarm_home, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = listAlarm[position]
        val ctx = holder.ctx
        var layout = holder.layoutBox
        holder.tvClock.text = showClock(data.hour, data.minute)
        holder.tvMedNam.text = data.medication
        holder.tvDose.text = data.dose.toString() + " " + data.unit
        var stat = holder.tvStat
        stat.text = data.status
        liveData.observe(lifecycleOwner, Observer {
            if(it.equals(data.medication)){
                medTakenLogic(data,layout, stat, ctx)
            }
        })

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

    fun setLiveDat(data:  MutableLiveData<String>){
        liveData = data
        Log.d("btDev", "live data loaded to adapter!")

    }

    private fun medTakenLogic(
        alarmData: AlarmData,
        layout: LinearLayout,
        stat: TextView,
        ctx: Context
    ){
        var isLate: Boolean

        val calendar: Calendar = Calendar.getInstance()
        val simpleDataFormat = SimpleDateFormat("HH:mm")
        val currentTime = simpleDataFormat.format(calendar.time)

        val calendar2: Calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarmData.hour)
            set(Calendar.MINUTE, alarmData.minute)
        }
        val timeShouldBe = simpleDataFormat.format(calendar2.time)

        val diff = calendar.timeInMillis - calendar2.timeInMillis
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60

        val minuteDiff = minutes
        val limit: Long = 100
        if (minuteDiff <= limit) {
            layout.setBackgroundColor(ContextCompat.getColor(ctx, R.color.light_green))
            stat.text = "Taken"
            isLate = false
        }
        else{
            layout.setBackgroundColor(ContextCompat.getColor(ctx, R.color.warning_red))
            stat.text = "Late"
            isLate = true
        }

        sendMedStateData(ctx)

    }

    private fun sendMedStateData(ctx: Context) {
        Toast.makeText(ctx, "HIT api", Toast.LENGTH_LONG).show()
    }

}