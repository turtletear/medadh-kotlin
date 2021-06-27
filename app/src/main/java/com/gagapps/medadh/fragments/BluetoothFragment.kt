package com.gagapps.medadh.fragments

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gagapps.medadh.R
import com.gagapps.medadh.btUtil.BluetoothDeviceDC
import com.gagapps.medadh.btUtil.btService.SendReceiveService
import com.gagapps.medadh.btUtil.btServiceThread.ClientThread
import com.gagapps.medadh.btUtil.rvAdapter.DeviceListAdapter
import kotlinx.android.synthetic.main.fragment_bluetooth.*
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BluetoothFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BluetoothFragment : Fragment(), CompoundButton.OnCheckedChangeListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var tvStatus: TextView
    lateinit var bAdapter: BluetoothAdapter
    lateinit var clThread: ClientThread
    lateinit var mSocket: BluetoothSocket
    private val REQUEST_CODE_ENABLE_BT: Int = 1
    private val FINE_LOCATION_PERMISSION_REQUEST: Int = 1001
    private val MY_UUID = UUID.fromString("91ce3659-1535-4b05-a89d-08ca023c8dd5")
    private var isBound: Boolean = false
    private var liveData = MutableLiveData<String>()

    lateinit var myService: SendReceiveService


    private lateinit var deviceList: Set<BluetoothDevice>

    //convert here . . .. .
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as SendReceiveService.LocalBinder
            myService = binder.service
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bluetooth, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BluetoothFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                BluetoothFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }//end companion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvDevice: RecyclerView = view.findViewById(R.id.rv_device_list)
        rvDevice.setHasFixedSize(true)
        try {
            bAdapter = BluetoothAdapter.getDefaultAdapter()
            btSwitch.setOnCheckedChangeListener(this)
            allowLocationDetectionPermissions()
            tvStatus = view.findViewById(R.id.tv_status)

            bt_scanDevice.setOnClickListener{
                btScan(rvDevice)
            }

            if (this::myService.isInitialized){
                liveData = myService.getServiceLiveData()
                Log.d("btDev", "it's worked!")
            }


        }catch (e: NullPointerException){
            Toast.makeText(activity, "Device doesn't support Bluetooth", Toast.LENGTH_LONG).show()
        }
    }


    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if(isChecked){
            btEnable()
        }
        else{
            btDisable()
        }
    }

    private fun btEnable(){
        if (bAdapter.isEnabled){
            Toast.makeText(activity, "Bluetooth is already enable", Toast.LENGTH_SHORT).show()
        }
        else {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
            tvStatus.text = "BT ON"
        }
    }//end func

    private fun btDisable(){
        if (!bAdapter.isEnabled){
            Toast.makeText(activity, "Bluetooth is already disable", Toast.LENGTH_SHORT).show()
        }
        else {
            bAdapter.disable()
            tv_status.text = "BT OFF"
        }
    }//end func

    private fun btScan(rvDevice: RecyclerView) {

        if (!bAdapter.isEnabled)
            Toast.makeText(activity, "Please Turn On the Bluetooth", Toast.LENGTH_SHORT).show()
        else{
            deviceList = bAdapter.bondedDevices
            val bdSize =  deviceList.size
            var btList = arrayListOf<BluetoothDevice>()
            if(bdSize > 0){
                for(device in deviceList){
                    btList.add(device)
                }
            }
            showRecyclerList(rvDevice, btList)
        }
    }

    private fun allowLocationDetectionPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION_PERMISSION_REQUEST)
        }
    }

    private fun showRecyclerList(rvDev: RecyclerView, deviceList: ArrayList<BluetoothDevice>){
        rvDev.layoutManager = LinearLayoutManager(requireContext())
        val deviceListAdapter = DeviceListAdapter(deviceList)
        rvDev.adapter = deviceListAdapter
        deviceListAdapter.setOnItemClickCallback(object : DeviceListAdapter.OnItemClickCallback{
            override fun onItemClicked(data: BluetoothDevice) {
                val intent = Intent(activity, SendReceiveService::class.java)
                intent.putExtra("MY_ADDRESS", data.address)
                activity?.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
//                clThread = ClientThread(data, bAdapter, MY_UUID, tvStatus, requireActivity())
//                clThread.start()
            }

        })
    }

}