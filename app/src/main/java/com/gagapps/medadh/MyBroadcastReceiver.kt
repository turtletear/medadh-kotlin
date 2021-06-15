package com.gagapps.medadh

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action.equals("com.gagapps.medadh.alarmmanager")){
            var bundle = intent.extras
            if (bundle != null) {
                    Log.d("Mantap", bundle.getString("Message").toString())
            }
            val notifyMe = Notifications()
            notifyMe.notify(context!!, "Take your medicine in time!")
        }
    }
}