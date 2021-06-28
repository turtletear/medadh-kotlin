package com.gagapps.medadh.alarmUtil

import android.content.Context
import android.content.SharedPreferences
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
import com.gagapps.medadh.ProfileDC
import com.gagapps.medadh.R
import com.gagapps.medadh.RetrofitInstance
import com.gagapps.medadh.dataClassMedState.SingleMedState.SingleMedState
import com.gagapps.medadh.dataClassMedState.bodyReq.*
import com.gagapps.medadh.interfaces.PatientsServices
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ListAlarmHomeAdapter(private val listAlarm: ArrayList<AlarmData>, val lifecycleOwner: LifecycleOwner, val profileData: ProfileDC?): RecyclerView.Adapter<ListAlarmHomeAdapter.ListViewHolder>() {

    private lateinit var onItemCheckedCallback: OnItemCheckedCallback
    private var liveData = MutableLiveData<String>()
    private var profDat = profileData

    interface OnItemCheckedCallback {
        fun onItemChecked(
            alarmData: AlarmData,
            position: Int,
            isChecked: Boolean,
            layout: LinearLayout,
            stat: TextView
        )
    }

    fun setOnItemCheckedCallback(onItemClickCallback: OnItemCheckedCallback) {
        this.onItemCheckedCallback = onItemClickCallback
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvClock: TextView = itemView.findViewById(R.id.tv_home_clock)
        var tvMedNam: TextView = itemView.findViewById(R.id.tv_home_med)
        var tvDose: TextView = itemView.findViewById(R.id.tv_home_dosage)
        var tvStat: TextView = itemView.findViewById(R.id.tv_home_status)
        var layoutBox: LinearLayout = itemView.findViewById(R.id.layout_box)
        var cbStat: CheckBox = itemView.findViewById(R.id.cb_take_med)
        var ctx = itemView.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_row_alarm_home, parent, false)
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
            if (it.equals(data.medication)) {
                medTakenLogic(data, layout, stat, ctx)
            }
        })

        holder.cbStat.setOnCheckedChangeListener { buttonView, isChecked ->
            onItemCheckedCallback.onItemChecked(
                listAlarm[holder.adapterPosition],
                position,
                isChecked,
                layout,
                stat
            )
        }
    }

    override fun getItemCount(): Int {
        return listAlarm.size
    }

    private fun showClock(hour: Int, minute: Int): String {
        var h = hour.toString()
        var m = minute.toString()

        if (hour < 10)
            h = "0" + h
        if (minute < 10)
            m = "0" + m

        return "${h}:${m}"
    }

    fun setLiveDat(data: MutableLiveData<String>) {
        liveData = data
        Log.d("btDev", "live data loaded to adapter!")

    }

    private fun medTakenLogic(
        alarmData: AlarmData,
        layout: LinearLayout,
        stat: TextView,
        ctx: Context
    ) {
        var isLate: Boolean

        val calendar: Calendar = Calendar.getInstance()
        val simpleDataFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
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
        val limit: Long = 210 //3.5 jam
        if (minuteDiff <= limit) {
            layout.setBackgroundColor(ContextCompat.getColor(ctx, R.color.light_green))
            stat.text = "Taken"
            isLate = false
        } else {
            layout.setBackgroundColor(ContextCompat.getColor(ctx, R.color.warning_red))
            stat.text = "Late"
            isLate = true
        }
        val coding = Coding(codeGenerator(alarmData.medication))

        val category = Category(coding, alarmData.medication)
        val dateAsrtd = currentTime
        val dose = Dosage(alarmData.dose.toString()+ " " + alarmData.unit)
        val effctv = Effective(timeShouldBe)
        val ext = Extension(isLate)
        val note = Note(alarmData.note)
        val statBR = Status("Taken")
        val subjct = profDat?.id!!

        val bodyReq = MedStateBodyReq(category, dateAsrtd, dose, effctv, ext, note, statBR, subjct)

        sendMedStateData(ctx, bodyReq)

    }

    private fun sendMedStateData(ctx: Context, bodyReq: MedStateBodyReq) {
        val token = profDat?.token
        val restService: PatientsServices = RetrofitInstance
            .getRetrofitInstance()
            .create(PatientsServices::class.java)
        val reqCall = restService.createMedState(bodyReq, "Bearer "+ token)
        reqCall.enqueue(object : Callback<SingleMedState>{
            override fun onResponse(call: Call<SingleMedState>, response: Response<SingleMedState>) {
                if (response.message() == "OK"){
                    Toast.makeText(ctx, "Save medState success!", Toast.LENGTH_LONG).show()
                    Log.d("medAdh", "Request status: ${response.body()?.status}")
                }
                else {
                    Toast.makeText(ctx, "Save report failed", Toast.LENGTH_LONG).show()
                    Log.d("medAdh", "Failed to save medState, status: ${response.body()?.status}")
                }
            }

            override fun onFailure(call: Call<SingleMedState>, t: Throwable) {
                Toast.makeText(ctx, "Save medState failed", Toast.LENGTH_LONG).show()
                Log.d("medAdh", "<ON FAILURE> Fail to save patient report data")
            }

        })


    }

    private fun codeGenerator(med: String): String {
        var coding = ""
        when(med){
            "Imatinib" -> coding = "414460008"
            "Nilotinib" -> coding = "427941004"
            "Dasatinib" -> coding = "423658008"
            "Ponatinib" -> coding = "703796003"
        }
        return coding
    }
}
