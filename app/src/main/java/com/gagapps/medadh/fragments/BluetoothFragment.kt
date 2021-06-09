package com.gagapps.medadh.fragments

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.gagapps.medadh.R
import com.gagapps.medadh.btUtilClass.BlueteethParcelable
import com.gagapps.medadh.fragments.dialogFragments.BtDevicesDialogFragment
import com.gagapps.medadh.loadingClass.LoadingDialog
import com.robotpajamas.blueteeth.BlueteethDevice
import com.robotpajamas.blueteeth.BlueteethManager
import com.robotpajamas.blueteeth.BlueteethResponse
import com.robotpajamas.blueteeth.BlueteethUtils
import com.robotpajamas.blueteeth.listeners.OnCharacteristicReadListener
import com.robotpajamas.blueteeth.listeners.OnCharacteristicWriteListener
import com.robotpajamas.blueteeth.listeners.OnServicesDiscoveredListener
import kotlinx.android.synthetic.main.fragment_bluetooth.*
import timber.log.Timber
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BluetoothFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BluetoothFragment : Fragment(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var bAdapter: BluetoothAdapter
    private val REQUEST_CODE_ENABLE_BT: Int = 1
    private val FINE_LOCATION_PERMISSION_REQUEST: Int = 1001
    private val DEVICE_SCAN_MILLISECONDS: Int = 3000
    private val SERVICE_UUID: UUID = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB")
    private val CHARACTERISTIC_UUID: UUID = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB")
    var CONNECT_STATUS: Int = 0

    private var deviceDataList: ArrayList<BlueteethParcelable> = arrayListOf() //save device name and address
    private var extBlueteethDevice: List<BlueteethDevice> = listOf()
    private lateinit var selectedBTDevice: BlueteethDevice
    private val dialog = BtDevicesDialogFragment()



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
        try {
            allowLocationDetectionPermissions()
            bAdapter = BluetoothAdapter.getDefaultAdapter()
            bt_scanDevice.setOnClickListener(this)
            btSwitch.setOnCheckedChangeListener(this)
            Log.d("Blueteeth", "receive this")

        }catch (e: NullPointerException){
            Toast.makeText(activity, "Device doesn't support Bluetooth", Toast.LENGTH_LONG).show()
        }


    }//end func

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.bt_scanDevice -> btScanBlueteeth()
        }
    }//end func


    private fun btEnable(){
        if (bAdapter.isEnabled){
            Toast.makeText(activity, "Bluetooth is already enable", Toast.LENGTH_SHORT).show()
        }
        else {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
        }
    }//end func

    private fun btDisable(){
        if (!bAdapter.isEnabled){
            Toast.makeText(activity, "Bluetooth is already disable", Toast.LENGTH_SHORT).show()
        }
        else {
            bAdapter.disable()
        }
    }//end func

    private fun btScanBlueteeth(){
        if (!bAdapter.isEnabled){
            Toast.makeText(activity, "Please turn on the Bluetooth", Toast.LENGTH_SHORT).show()
        }
        else {
            val loading = LoadingDialog(requireActivity())
            val bundle = Bundle()
            loading.startLoading()
            BlueteethManager.with(context).scanForPeripherals(
                DEVICE_SCAN_MILLISECONDS
            ) { blueteethDevices: List<BlueteethDevice> ->
                // Scan completed, iterate through received devices and log their name/mac address
                for (device in blueteethDevices) {
                    if (!TextUtils.isEmpty(device.bluetoothDevice.name)) {
                        val deviceParcelable = BlueteethParcelable(device.name, device.macAddress)
                        deviceDataList.add(deviceParcelable)
                    }
                }
                extBlueteethDevice = blueteethDevices
                bundle.putParcelableArrayList("DeviceList", deviceDataList )
                dialog.arguments = bundle
                val mFragmentManager = childFragmentManager
                loading.isDismiss()
                dialog.isCancelable = false
                dialog.show(mFragmentManager, BtDevicesDialogFragment::class.java.simpleName)
            }
        }//end if
    }//end func


    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if(isChecked){
            btEnable()
            Toast.makeText(activity, "Bluetooth turned on", Toast.LENGTH_SHORT).show()
        }
        else{
            btDisable()
            Toast.makeText(activity, "Bluetooth turned off", Toast.LENGTH_SHORT).show()
        }
    }

    private fun allowLocationDetectionPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION_PERMISSION_REQUEST)
        }
    }

    private fun connectDevice(device: BlueteethParcelable?): BlueteethDevice? {
        val foundDevice: BlueteethDevice? = searchDeviceWithAddress(device?.deviceAddress)
        if(foundDevice != null){
            foundDevice.connect(false) { isConnected ->
                Log.d("Blueteeth", "Is Connected: ${isConnected}")
                discoverService(foundDevice)
                sendData(foundDevice,"1")
            }
            CONNECT_STATUS = 1
            selectedBTDevice = foundDevice
            Toast.makeText( requireContext(), "Connected to:  " + device?.deviceName, Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText( requireContext(), "Failed to connect to:  " + device?.deviceName, Toast.LENGTH_SHORT).show()
        }
        dialog.dismiss()
        deviceDataList.clear()
        BlueteethManager.with(context).stopScanForPeripherals()
        return foundDevice
    }
    private fun searchDeviceWithAddress(address: String?): BlueteethDevice? {
        var result: BlueteethDevice? = null
        for (device in extBlueteethDevice){
            if(device.macAddress.equals(address)){
                result = device
                break
            }
        }
        return result
    }
    private fun discoverService(device: BlueteethDevice?){
        try {
            device?.discoverServices{ response ->
                if (response != BlueteethResponse.NO_ERROR) {
                    Log.e("Blueteeth", "Discovery error ${response.name}")
                }
                Log.d("Blueteeth", "Discovered services... Can now try to read/write...") }
        }catch (e: IOException){
            Log.e("Blueteeth", "Discovery error, ${e.message}")
        }

    }

    private fun sendData(device: BlueteethDevice?, data: String){
        val dataSend = data.toByteArray()
        try {
            if (device != null) {
                BlueteethUtils.writeData(dataSend, CHARACTERISTIC_UUID, SERVICE_UUID, device, OnCharacteristicWriteListener { response ->
                    if (response != BlueteethResponse.NO_ERROR){
                        Log.e("Blueteeth", "Write characteristic error!")
                    }
                    Log.d("Blueteeth", "Characteristic sent!")
                })
            }
        } catch (e: IOException){
            Log.e("Blueteeth", "Error ${e.message}")
        }
    }

    private fun receiveData(device: BlueteethDevice?){
        try {
            if (device != null) {
                BlueteethUtils.read(CHARACTERISTIC_UUID, SERVICE_UUID, device, OnCharacteristicReadListener { response, data ->
                    if (response != BlueteethResponse.NO_ERROR){
                        Log.e("Blueteeth", "Write characteristic error!")
                    }
                    Log.d("Blueteeth", "Data receive: ${data}")
                })
            }
        } catch (e: IOException){
            Log.e("Blueteeth", "Error ${e.message}")
        }
    }

    internal var optionDialogListener: BtDevicesDialogFragment.OnOptionDialogListener = object :BtDevicesDialogFragment.OnOptionDialogListener{
        override fun onDeviceSelect(device: BlueteethParcelable) {
            val bleDevice = device
            val connectedDevice = connectDevice(bleDevice)
        }

    }

    internal var cancelButtonListener: BtDevicesDialogFragment.OnCancelButtonListener = object :BtDevicesDialogFragment.OnCancelButtonListener{
        override fun onCancelButtonPress() {
            dialog.dismiss()
            deviceDataList.clear()
            BlueteethManager.with(context).stopScanForPeripherals()
        }

    }

}