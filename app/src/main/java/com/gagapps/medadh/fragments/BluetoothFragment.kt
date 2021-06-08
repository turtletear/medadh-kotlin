package com.gagapps.medadh.fragments

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
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
import com.gagapps.medadh.fragments.dialogFragments.BtDevicesDialogFragment
import com.gagapps.medadh.loadingClass.LoadingDialog
import kotlinx.android.synthetic.main.fragment_bluetooth.*
import java.lang.NullPointerException


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
    private var deviceList: ArrayList<ScanResult> = arrayListOf()
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
            bAdapter = BluetoothAdapter.getDefaultAdapter()
            bt_scanDevice.setOnClickListener(this)
            btSwitch.setOnCheckedChangeListener(this)
        }catch (e: NullPointerException){
            Toast.makeText(activity, "Device doesn't support Bluetooth", Toast.LENGTH_LONG).show()
        }
//        val checkBT = context?.packageManager?.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
//        Log.d("bleDevice", checkBT.toString())
//        if (bAdapter == null){
//            Toast.makeText(activity, "Device doesn't support Bluetooth", Toast.LENGTH_LONG).show()
//        }

    }//end func

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.bt_scanDevice -> btScan()
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

    private fun  btScan(){
        val bluetoothLeScanner: BluetoothLeScanner? = bAdapter.bluetoothLeScanner
        var scanning = false
        val handler = Handler(Looper.getMainLooper())
        val SCAN_PERIOD: Long = 5000


        if (!bAdapter.isEnabled){
            Toast.makeText(activity, "Please turn on the Bluetooth", Toast.LENGTH_SHORT).show()
        }//end if
        else {
            allowLocationDetectionPermissions()
            bluetoothLeScanner?.let { scanner ->
                if (!scanning) { // Stops scanning after a pre-defined scan period.
                    handler.postDelayed({
                        scanning = false
                        scanner.stopScan(leScanCallback)
                    }, SCAN_PERIOD)
                    scanning = true
                    scanner.startScan(leScanCallback)
                } else {
                    scanning = false
                    scanner.stopScan(leScanCallback)
                }
            }
            val loading = LoadingDialog(requireActivity())
            val bundle = Bundle()
            bundle.putParcelableArrayList("DeviceList", deviceList)

            val mFragmentManager = childFragmentManager
            loading.startLoading()
            handler.postDelayed({
                dialog.arguments = bundle
                loading.isDismiss()
                dialog.isCancelable = false
                dialog.show(mFragmentManager!!, BtDevicesDialogFragment::class.java.simpleName)
            }, SCAN_PERIOD)

        }//end else
    }//end func

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            deviceList.add(result)
        }
    }

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

    private fun connectDevice(device: BluetoothDevice?){
        Toast.makeText( requireContext(), "Connect to:  " + device?.name, Toast.LENGTH_SHORT).show()
        dialog.dismiss()
    }

    internal var optionDialogListener: BtDevicesDialogFragment.OnOptionDialogListener = object :BtDevicesDialogFragment.OnOptionDialogListener{
        override fun onDeviceSelect(device: ScanResult?) {
            val bleDevice = device?.device
            val listId: MutableList<ParcelUuid>? = device?.scanRecord?.serviceUuids
            Log.d("bleDevice", "list len: {${listId?.get(0)}} ")
            connectDevice(bleDevice)
        }

    }

    private fun gattCallback(){

    }

}