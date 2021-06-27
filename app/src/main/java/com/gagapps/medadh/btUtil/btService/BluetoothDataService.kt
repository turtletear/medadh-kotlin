package com.gagapps.medadh.btUtil.btService

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothDataService : Service() {
    val handlerState = 0 //used to identify handler message
    var bluetoothIn: Handler? = null
    private var btAdapter: BluetoothAdapter? = null
    private var mConnectingThread: ConnectingThread? = null
    private var mConnectedThread: ConnectedThread? = null
    private var MAC_ADDRESS = ""
    private val KEY_MAC_ADDRESS = "MAC_ADDRESS"
    private val binder = LocalBinder()
    private var stopThread = false
    private val recDataString = StringBuilder()


    override fun onCreate() {
        super.onCreate()
        Log.d("btDev", "SERVICE CREATED")
        stopThread = false
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("btDev", "SERVICE STARTED")
        bluetoothIn = object : Handler() {
            override fun handleMessage(msg: Message) {
                Log.d("DEBUG", "handleMessage")
                if (msg.what == handlerState) {                                     //if message is what we want
                    val readMessage = msg.obj as String // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage)
                    Log.d("RECORDED", recDataString.toString())
                    // Do stuff here with your data, like adding it to the database
                }
                recDataString.delete(0, recDataString.length) //clear all string data
            }
        }
        btAdapter = BluetoothAdapter.getDefaultAdapter() // get Bluetooth adapter
        checkBTState()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothIn!!.removeCallbacksAndMessages(null)
        stopThread = true
        if (mConnectedThread != null) {
            mConnectedThread!!.closeStreams()
        }
        if (mConnectingThread != null) {
            mConnectingThread!!.closeSocket()
        }
        Log.d("SERVICE", "onDestroy")
    }

    override fun onBind(intent: Intent): IBinder? {
        val params = intent.extras
        MAC_ADDRESS = params?.get(KEY_MAC_ADDRESS) as String
        return binder
    }

    inner class LocalBinder : Binder() {
        val service: BluetoothDataService
            get() = this@BluetoothDataService
    }



    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private fun checkBTState() {
        if (btAdapter == null) {
            Log.d("btDev", "BLUETOOTH NOT SUPPORTED BY DEVICE, STOPPING SERVICE")
            stopSelf()
        } else {
            if (btAdapter!!.isEnabled) {
                Log.d(
                    "btDev",
                    "BT ENABLED! BT ADDRESS : " + btAdapter!!.address + " , BT NAME : " + btAdapter!!.name
                )
                try {
                    val device = btAdapter!!.getRemoteDevice(MAC_ADDRESS)
                    Log.d("btDev", "ATTEMPTING TO CONNECT TO REMOTE DEVICE : " + MAC_ADDRESS)
                    mConnectingThread = ConnectingThread(device)
                    mConnectingThread!!.start()
                } catch (e: IllegalArgumentException) {
                    Log.d("btDev", "PROBLEM WITH MAC ADDRESS : $e")
                    Log.d("BT SEVICE", "ILLEGAL MAC ADDRESS, STOPPING SERVICE")
                    stopSelf()
                }
            } else {
                Log.d("btDev", "BLUETOOTH NOT ON, STOPPING SERVICE")
                stopSelf()
            }
        }
    }

    // New Class for Connecting Thread
    private inner class ConnectingThread(device: BluetoothDevice) : Thread() {
        private val mmSocket: BluetoothSocket?
        private val mmDevice: BluetoothDevice
        override fun run() {
            super.run()
            Log.d("btDev", "IN CONNECTING THREAD RUN")
            // Establish the Bluetooth socket connection.
            // Cancelling discovery as it may slow down connection
            btAdapter!!.cancelDiscovery()
            try {
                mmSocket!!.connect()
                Log.d("btDev", "BT SOCKET CONNECTED")
                mConnectedThread = ConnectedThread(mmSocket)
                mConnectedThread!!.start()
                Log.d("btDev", "CONNECTED THREAD STARTED")
                //I send a character when resuming.beginning transmission to check device is connected
                //If it is not an exception will be thrown in the write method and finish() will be called
                mConnectedThread!!.write("x")
            } catch (e: IOException) {
                try {
                    Log.d("btDev", "SOCKET CONNECTION FAILED : $e")
                    Log.d("btDev", "SOCKET CONNECTION FAILED, STOPPING SERVICE")
                    mmSocket!!.close()
                    stopSelf()
                } catch (e2: IOException) {
                    Log.d("btDev", "SOCKET CLOSING FAILED :$e2")
                    Log.d("btDev", "SOCKET CLOSING FAILED, STOPPING SERVICE")
                    stopSelf()
                    //insert code to deal with this
                }
            } catch (e: IllegalStateException) {
                Log.d("btDev", "CONNECTED THREAD START FAILED : $e")
                Log.d("btDev", "CONNECTED THREAD START FAILED, STOPPING SERVICE")
                stopSelf()
            }
        }

        fun closeSocket() {
            try {
                //Don't leave Bluetooth sockets open when leaving activity
                mmSocket!!.close()
            } catch (e2: IOException) {
                //insert code to deal with this
                Log.d("btDev", e2.toString())
                Log.d("btDev", "SOCKET CLOSING FAILED, STOPPING SERVICE")
                stopSelf()
            }
        }

        init {
            Log.d("btDev", "IN CONNECTING THREAD")
            mmDevice = device
            var temp: BluetoothSocket? = null
            Log.d("btDev", "MAC ADDRESS : " + MAC_ADDRESS)
            Log.d("btDev", "BT UUID : " + BTMODULEUUID)
            try {
                temp = mmDevice.createRfcommSocketToServiceRecord(BTMODULEUUID)
                Log.d("btDev", "SOCKET CREATED : $temp")
            } catch (e: IOException) {
                Log.d("btDev", "SOCKET CREATION FAILED :$e")
                Log.d("btDev", "SOCKET CREATION FAILED, STOPPING SERVICE")
                stopSelf()
            }
            mmSocket = temp
        }
    }

    // New Class for Connected Thread
    private inner class ConnectedThread(socket: BluetoothSocket?) : Thread() {
        private val mmInStream: InputStream?
        private val mmOutStream: OutputStream?
        override fun run() {
            Log.d("btDev", "IN CONNECTED THREAD RUN")
            val buffer = ByteArray(256)
            var bytes: Int

            // Keep looping to listen for received messages
            while (true && !stopThread) {
                try {
                    bytes = mmInStream!!.read(buffer) //read bytes from input buffer
                    val readMessage = String(buffer, 0, bytes)
                    Log.d("btDev PART", "CONNECTED THREAD $readMessage")
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn!!.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget()
                } catch (e: IOException) {
                    Log.d("btDev", e.toString())
                    Log.d("btDev", "UNABLE TO READ/WRITE, STOPPING SERVICE")
                    stopSelf()
                    break
                }
            }
        }

        //write method
        fun write(input: String) {
            val msgBuffer = input.toByteArray() //converts entered String into bytes
            try {
                mmOutStream!!.write(msgBuffer) //write bytes over BT connection via outstream
            } catch (e: IOException) {
                //if you cannot write, close the application
                Log.d("btDev", "UNABLE TO READ/WRITE $e")
                Log.d("btDev", "UNABLE TO READ/WRITE, STOPPING SERVICE")
                stopSelf()
            }
        }

        fun closeStreams() {
            try {
                //Don't leave Bluetooth sockets open when leaving activity
                mmInStream!!.close()
                mmOutStream!!.close()
            } catch (e2: IOException) {
                //insert code to deal with this
                Log.d("btDev", e2.toString())
                Log.d("btDev", "STREAM CLOSING FAILED, STOPPING SERVICE")
                stopSelf()
            }
        }

        //creation of the connect thread
        init {
            Log.d("btDev", "IN CONNECTED THREAD")
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null
            try {
                //Create I/O streams for connection
                tmpIn = socket!!.inputStream
                tmpOut = socket.outputStream
            } catch (e: IOException) {
                Log.d("btDev", e.toString())
                Log.d("btDev", "UNABLE TO READ/WRITE, STOPPING SERVICE")
                stopSelf()
            }
            mmInStream = tmpIn
            mmOutStream = tmpOut
        }
    }

    companion object {
        // SPP UUID service - this should work for most devices
        private val BTMODULEUUID = UUID.fromString("91ce3659-1535-4b05-a89d-08ca023c8dd5")
    }
}