package com.gagapps.medadh.btUtil.btServiceThread

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import android.widget.TextView
import java.io.IOException
import java.util.*
import android.os.Handler
import android.os.Looper

class ClientThread(device: BluetoothDevice, bAdapter: BluetoothAdapter, myuuid: UUID, tvStatus: TextView, context: Activity) : Thread() {
    val adapter = bAdapter
    lateinit var soket: BluetoothSocket
    val cntxt = context
    val tvStat = tvStatus
    private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE){
        device.createRfcommSocketToServiceRecord(myuuid)
    }

    override fun run() {
        adapter?.cancelDiscovery()
        mmSocket?.let { socket ->
            try {
                socket.connect()
                //manageMyConnectedSocket(socket)
                //initiate service class here..
                //val handler = Handler(Looper.getMainLooper())
                //val services = SendReceive(socket, handler)
                //services.start()
                //soket = socket
                //Log.d("btDev", "connected")
                //cntxt.runOnUiThread {
                    //tvStat.text = "Connected"
                //}
            }catch (e: IOException){
                e.printStackTrace()
                Log.e("btDev", "Message: ${e}")
            }
        }
    }

    fun cancel(){
        try {
            mmSocket?.close()
        } catch (e: IOException){
            Log.e("btDev", "Could not close the client socket", e)
        }
    }

    fun getSocket(): BluetoothSocket {
        return soket
    }
}