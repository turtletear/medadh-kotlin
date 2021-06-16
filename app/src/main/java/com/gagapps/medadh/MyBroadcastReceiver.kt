package com.gagapps.medadh

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class MyBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val calendar: Calendar = Calendar.getInstance()
        val simpleDataFormat: SimpleDateFormat = SimpleDateFormat("HH:mm")
        val currentTime = simpleDataFormat.format(calendar.time)
        val msg = " Notif Receive at: "+currentTime

        if(intent.action.equals("com.gagapps.medadh.alarmmanager")){
            var bundle = intent.extras
            if (bundle != null) {
                Log.d("Mantap", bundle.getString("Message").toString())
                val notifyMe = Notifications()
                notifyMe.notify(context!!, bundle.getString("Message").toString()+ msg)
            }
        }
    }
}