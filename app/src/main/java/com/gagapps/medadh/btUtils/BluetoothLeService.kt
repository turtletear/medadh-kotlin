package com.gagapps.medadh.btUtils

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class BluetoothLeService : Service() {

    private val binder = LocalBinder()
    private var bluetoothAdapter: BluetoothAdapter? = null

    fun initialize(): Boolean {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.e("bleDevice", "Unable to obtain a BluetoothAdapter.")
            return false
        }
        return true
    }
    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService() : BluetoothLeService {
            return this@BluetoothLeService
        }
    }

    fun connect(address: String?): Boolean{
        Log.d("bleDevice", "Masuk sini")
       var retVal: Boolean = true
        bluetoothAdapter?.let { adapter ->
            try {
                val device = adapter.getRemoteDevice(address)
            } catch (exception: IllegalArgumentException) {
                Log.w("bleDevice", "Device not found with provided address.")
                retVal = false
            }
            // connect to the GATT server on the device
        } ?: run {
            Log.w("bleDevice", "BluetoothAdapter not initialized")
            retVal  = false
        }
        return retVal
    }
}