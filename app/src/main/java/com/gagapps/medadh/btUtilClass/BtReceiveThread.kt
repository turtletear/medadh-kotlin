package com.gagapps.medadh.btUtilClass

import android.util.Log
import com.robotpajamas.blueteeth.BlueteethDevice
import com.robotpajamas.blueteeth.BlueteethResponse
import com.robotpajamas.blueteeth.BlueteethUtils
import com.robotpajamas.blueteeth.listeners.OnCharacteristicReadListener
import java.util.*

class BtReceiveThread(device: BlueteethDevice?, charUUID: UUID, servUUID: UUID): Thread() {
    val btDevice = device
    val CHARACTERISTIC_UUID = charUUID
    val SERVICE_UUID = servUUID
    override fun run() {
        super.run()
        Thread.sleep(5000)
        try {
            if (btDevice != null) {
                BlueteethUtils.read(CHARACTERISTIC_UUID, SERVICE_UUID, btDevice, OnCharacteristicReadListener { response, data ->
                    if (response != BlueteethResponse.NO_ERROR){
                        Log.e("Blueteeth", "Read characteristic error!")
                    }
                    while (data != null){
                        Thread.sleep(1000)
                        Log.d("Blueteeth", "Data receive: ${data}")
                    }
                })
            }
        } catch (e: Exception){
                e.printStackTrace()
        }

    }
}