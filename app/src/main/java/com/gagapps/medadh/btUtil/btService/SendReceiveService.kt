package com.gagapps.medadh.btUtil.btService

import android.app.*
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.text.style.BulletSpan
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.gagapps.medadh.PatientDashboardActivity
import com.gagapps.medadh.R
import com.gagapps.medadh.btUtil.btServiceThread.SendReceive

class SendReceiveService(): Service() {
    private lateinit var thread: SendReceive

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        thread.start()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }



}