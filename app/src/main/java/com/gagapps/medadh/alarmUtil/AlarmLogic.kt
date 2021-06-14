package com.gagapps.medadh.alarmUtil

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gagapps.medadh.MyBroadcastReceiver
import java.util.*

class AlarmLogic {
    var context: Context? = null
    lateinit var alarmManager: AlarmManager
    lateinit var intent: Intent
    lateinit var pendIntent: PendingIntent

    constructor (context: Context){
        this.context = context
        this.alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        this.intent = Intent(context, MyBroadcastReceiver::class.java)
        intent.putExtra("Message", "Alarm Time")
        intent.action = "com.gagapps.medadh.alarmmanager"
        this.pendIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun SetAlarm(hour: Int, minute: Int){
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }


        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendIntent)

        Log.d("Mantap", "set alarm complete")
    }

    fun cancelAlarm(){
        alarmManager.cancel(pendIntent)
    }
}