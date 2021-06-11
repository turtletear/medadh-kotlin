package com.gagapps.medadh.fragments.dialogFragments

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gagapps.medadh.R
import com.gagapps.medadh.adapters.DeviceListAdapter
import com.gagapps.medadh.btUtils.RxBleParcelable
import com.gagapps.medadh.fragments.BluetoothFragment
import kotlinx.android.synthetic.main.fragment_bt_devices_dialog.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BtDevicesDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BtDevicesDialogFragment : DialogFragment() {

    private var devices: ArrayList<RxBleParcelable>? = null
    private var optionDialogListener: OnOptionDialogListener? = null
    private var cancelButtonListener: OnCancelButtonListener? = null

    interface OnOptionDialogListener {
        fun onDeviceSelect(device: RxBleParcelable?)
    }
    interface OnCancelButtonListener{
        fun onCancelButtonPress()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val parent = parentFragment
        if(parent is BluetoothFragment){
            val bluetoothFragment = parent
            this.optionDialogListener = bluetoothFragment.optionDialogListener
            this.cancelButtonListener = bluetoothFragment.cancelButtonListener
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.optionDialogListener = null
        this.cancelButtonListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            devices = it.getParcelableArrayList("DeviceList")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bt_devices_dialog, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BtDevicesDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BtDevicesDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }//end companion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_bt_list.setHasFixedSize(true)
        showRecyclerList()

        fd_cancel.setOnClickListener { buttonCancel() }

    }

    private fun showRecyclerList() {
        rv_bt_list.layoutManager = LinearLayoutManager(context)
        val listHeroAdapter = DeviceListAdapter(devices)
        rv_bt_list.adapter = listHeroAdapter
        listHeroAdapter.setOnItemClickCallback(object : DeviceListAdapter.OnItemClickCallback{
            override fun onItemClicked(data: RxBleParcelable) {
                selectDevice(data)
            }

        })

    }

    private fun selectDevice(device: RxBleParcelable){
        if(optionDialogListener != null){
            optionDialogListener?.onDeviceSelect(device)
        }
    }

    private fun buttonCancel(){
        if(cancelButtonListener != null){
            cancelButtonListener?.onCancelButtonPress()
        }
    }

}