package com.gagapps.medadh.btUtil.btService

import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.gagapps.medadh.btUtil.btServiceThread.STATE_MESSAGE_RECEIVED
import com.gagapps.medadh.btUtil.btServiceThread.SendReceive
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*


class SendReceiveService(): Service() {
    companion object{
        var serviceStat = 0
    }

    private val MY_UUID = UUID.fromString("91ce3659-1535-4b05-a89d-08ca023c8dd5")
    private var MY_ADDRESS: String? = null
    private val binder = LocalBinder()
    private var bAdapter: BluetoothAdapter? = null
    private var device: BluetoothDevice? = null
    private lateinit var soket: BluetoothSocket
    var liveData = MutableLiveData<String>()




    override fun onBind(intent: Intent): IBinder? {
        MY_ADDRESS = intent.getStringExtra("MY_ADDRESS")
        bAdapter = BluetoothAdapter.getDefaultAdapter()
        serviceStat = 1
        estabilishConnection()
        return binder
    }

    inner class LocalBinder : Binder() {
        val service: SendReceiveService
            get() = this@SendReceiveService
    }

    private fun estabilishConnection(){
        bAdapter?.cancelDiscovery()
        device = bAdapter?.getRemoteDevice(MY_ADDRESS)
        val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE){
            device?.createRfcommSocketToServiceRecord(MY_UUID)
        }
        mmSocket?.let {socket ->
            try {
                socket.connect()
                soket = socket
                startListen()
                Log.d("btDev", "Connect success, name: ${device?.name}")
            } catch (e: IOException){
                e.printStackTrace()
                Log.e("btDev", "Failed to connect socket Message: ${e}")
            }

        }

    }

    fun startListen(){
        val socket = soket
        val srThread = SendReceive(socket)
        srThread.start()
        liveData = srThread.getLiveDat()
    }

    fun getServiceLiveData(): MutableLiveData<String> {
        return liveData
    }

    fun getSckt(): BluetoothSocket {
        return soket
    }

}