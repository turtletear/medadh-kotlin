package com.gagapps.medadh.btUtilClass

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleDevice
import com.robotpajamas.blueteeth.BlueteethDevice
import com.robotpajamas.blueteeth.BlueteethManager
import com.robotpajamas.blueteeth.BlueteethResponse
import com.robotpajamas.blueteeth.BlueteethUtils
import com.robotpajamas.blueteeth.listeners.OnCharacteristicReadListener
import java.util.*

class BtReceiveThread(device: BlueteethDevice?, charUUID: UUID, servUUID: UUID, mContext: Context?): Thread() {
    val btDevice = device
    val CHARACTERISTIC_UUID = charUUID
    val SERVICE_UUID = servUUID

    //coba rxBle
    val rxBleClient =  RxBleClient.create(mContext!!)
    val rxDevice: RxBleDevice = rxBleClient.getBleDevice(btDevice?.macAddress!!)
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
                        Thread.sleep(2000)
                        Log.d("Blueteeth", "Data receive: ${data}")
                    }
                })
            }//end if
        } catch (e: Exception){
                e.printStackTrace()
        }

    }

}