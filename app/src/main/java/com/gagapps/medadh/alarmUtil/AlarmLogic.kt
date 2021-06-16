package com.gagapps.medadh.alarmUtil

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gagapps.medadh.MyBroadcastReceiver
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*
class AlarmLogic {
    @Transient
    lateinit var context: Context

    var intent: Intent

    var pendIntent: PendingIntent

    @Transient
    var alarmManager: AlarmManager

    var alarmData: AlarmData
    var reqCode : Int

    constructor (context: Context, alarmData: AlarmData){
        this.context = context
        this.alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        this.intent = Intent(context, MyBroadcastReceiver::class.java)
        this.alarmData = alarmData
        this.reqCode = alarmData.reqCode

        val msgVal = alarmData.medication

        intent.putExtra("Message", msgVal)
        intent.action = "com.gagapps.medadh.alarmmanager"
        this.pendIntent = PendingIntent.getBroadcast(context, alarmData.reqCode , intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun setAlarm(){
        val hour = alarmData.hour
        val minute = alarmData.minute
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if(System.currentTimeMillis() > calendar.getTimeInMillis())
            {
                calendar.add(Calendar.DAY_OF_YEAR, 1);   ///to avoid firing the alarm immediately
            }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendIntent)
        Log.d("Mantap", "Alarm saved! Alarm ReqCode: ${alarmData.reqCode}")
    }

    fun getAlrmManager(): AlarmManager {
        return alarmManager
    }

    fun getAlrmData(): AlarmData {
        return alarmData
    }

    fun cancelAlarm(){
        alarmManager.cancel(pendIntent)
    }
}