package com.gagapps.medadh.fragments

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.gagapps.medadh.btUtils.BluetoothLeService
import com.gagapps.medadh.btUtils.RxBleParcelable
import com.gagapps.medadh.fragments.dialogFragments.BtDevicesDialogFragment
import com.gagapps.medadh.loadingClass.LoadingDialog
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.scan.ScanSettings
import kotlinx.android.synthetic.main.fragment_bluetooth.*
import java.util.*


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
    private var deviceList: ArrayList<RxBleDevice> = arrayListOf()
    private val dialog = BtDevicesDialogFragment()
    private var bluetoothService : BluetoothLeService? = null
    lateinit var rxBleClient: RxBleClient
    private var deviceDataList: ArrayList<RxBleParcelable> = arrayListOf() //save device name and address
    private var extRxBleDevice: List<RxBleDevice> = listOf() //save device



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
            rxBleClient = RxBleClient.create(context!!)
            bt_scanDevice.setOnClickListener(this)
            btSwitch.setOnCheckedChangeListener(this)
            allowLocationDetectionPermissions()
        }catch (e: NullPointerException){
            Toast.makeText(activity, "Device doesn't support Bluetooth", Toast.LENGTH_LONG).show()
        }

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
        val handler = Handler(Looper.getMainLooper())
        val SCAN_PERIOD: Long = 2000

        if (!bAdapter.isEnabled){
            Toast.makeText(activity, "Please turn on the Bluetooth", Toast.LENGTH_SHORT).show()
        }//end if
        else {
            val loading = LoadingDialog(requireActivity())
            val bundle = Bundle()

            loading.startLoading()
            val scanSubscription = rxBleClient!!.scanBleDevices(
                ScanSettings.Builder()
                    .build()
            )
                .subscribe(
                    { scanResult: com.polidea.rxandroidble2.scan.ScanResult? ->
                        if (scanResult != null) {
                            val deviceParcelable = RxBleParcelable(scanResult.bleDevice.name, scanResult.bleDevice.macAddress)
                            deviceDataList.add(deviceParcelable)
                            deviceList.add(scanResult.bleDevice)
                        }
                    }
                ) { throwable: Throwable? ->
                        Log.e("rxBLE", "Error obtaining device")
                }

            handler.postDelayed({
                loading.isDismiss()
                bundle.putParcelableArrayList("DeviceList", deviceDataList)
                scanSubscription.dispose()
                dialog.arguments = bundle
                dialog.isCancelable = false
                val mFragmentManager = childFragmentManager
                dialog.show(mFragmentManager!!, BtDevicesDialogFragment::class.java.simpleName)
                Log.d("rxBLE", "Device data obtain : ${deviceDataList.size}")
                Log.d("rxBLE", "Device obtain : ${deviceList.size}")
            }, SCAN_PERIOD)

        }//end else
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

    private fun connectDevice(device: RxBleParcelable?){
        try {
            val macAddress = device?.adress
            val device = macAddress?.let { rxBleClient.getBleDevice(it) }

            val disposable = device?.establishConnection(false) // <-- autoConnect flag
                ?.subscribe(
                    { rxBleConnection: RxBleConnection? ->
                        Log.d("rxBLE", "Connected to: ${device?.name}")
                        Toast.makeText( requireContext(), "Connected to:  " + device?.name, Toast.LENGTH_SHORT).show()
                    }
                ) { throwable: Throwable? ->
                    Log.e("rxBLE", "Failed to connect to: ${device?.name}")
                    throwable?.printStackTrace()
                }
        } catch (e: Exception){
            e.printStackTrace()
        }

        dialog.dismiss()
        deviceDataList.clear()
        deviceList.clear()

    }




    internal var optionDialogListener: BtDevicesDialogFragment.OnOptionDialogListener = object :BtDevicesDialogFragment.OnOptionDialogListener{
        override fun onDeviceSelect(device: RxBleParcelable?) {
            connectDevice(device)
        }

    }

    internal var cancelButtonListener: BtDevicesDialogFragment.OnCancelButtonListener = object :BtDevicesDialogFragment.OnCancelButtonListener{
        override fun onCancelButtonPress() {
            dialog.dismiss()
            deviceDataList.clear()
            deviceList.clear()

        }

    }


}