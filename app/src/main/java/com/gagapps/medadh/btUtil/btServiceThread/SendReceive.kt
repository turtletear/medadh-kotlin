package com.gagapps.medadh.btUtil.btServiceThread

import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData

const val STATE_MESSAGE_RECEIVED: Int = 0

internal class SendReceive(private val bluetoothSocket: BluetoothSocket) : Thread() {
    private val inputStream: InputStream?
    private val outputStream: OutputStream?
    private var liveData = MutableLiveData<String>()
    override fun run() {
        val BUFFER_SIZE = 1024
        val buffer = ByteArray(BUFFER_SIZE)
        var bytes = 0
        while (true) {
            try {
                bytes = inputStream!!.read(buffer)
                val readMsg = String(buffer, 0, bytes)
                Log.d("btDev", "Data : ${readMsg}")
                liveData.postValue(readMsg)
//                handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget()
            } catch (e: IOException) {
                e.printStackTrace()
                break
            }
        }
    }

    fun getLiveDat(): MutableLiveData<String> {
        return liveData
    }


    init {
        var tempIn: InputStream? = null
        var tempOut: OutputStream? = null
        try {
            tempIn = bluetoothSocket.inputStream
            tempOut = bluetoothSocket.outputStream
        } catch (e: IOException) {
            e.printStackTrace()
        }
        inputStream = tempIn
        outputStream = tempOut
    }
}