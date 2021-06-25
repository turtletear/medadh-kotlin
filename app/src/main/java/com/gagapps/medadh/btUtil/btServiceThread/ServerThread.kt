package com.gagapps.medadh.btUtil.btServiceThread

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.*

class ServerThread(bAdapter: BluetoothAdapter, appName: String, myuuid: UUID) : Thread() {
    private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE){
        bAdapter?.listenUsingInsecureRfcommWithServiceRecord(appName, myuuid)
    }

    override fun run() {
        var shouldLoop = true
        while (shouldLoop) {
            val socket: BluetoothSocket? = try {
                mmServerSocket?.accept()
            } catch (e: IOException) {
                Log.e("btDev", "Socket's accept() method failed", e)
                shouldLoop = false
                null
            }
            socket?.also {
                //manageMyConnectedSocket(it)
                //manage wether receive/send data
                Log.d("btDev",  "Server connected")
                mmServerSocket?.close()
                shouldLoop = false
            }
        }
    }

    fun cancel(){
        try {
            mmServerSocket?.close()
        }catch(e: IOException){
            e.printStackTrace()
            Log.e("btDev", "Could not close the connect socket ", e)
        }
    }
}