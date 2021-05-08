package com.gagapps.medadh.adapters

import android.bluetooth.le.ScanResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gagapps.medadh.R


class DeviceListAdapter(private val listDevice: ArrayList<ScanResult>?): RecyclerView.Adapter<DeviceListAdapter.ListViewHolder>() {

    lateinit var onItemClickCallback: OnItemClickCallback

    @JvmName("setOnItemClickCallback1")
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvDeviceName: TextView = itemView.findViewById(R.id.tv_device_name)
        var tvDeviceAddress: TextView = itemView.findViewById(R.id.tv_device_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_bt_list_row, parent, false)
        return ListViewHolder(view)
    }


    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val device = listDevice?.get(position)

        if (device != null) {
            holder.tvDeviceName.text = device.device.name
            holder.tvDeviceAddress.text = device.device.address
        }
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listDevice!![holder.bindingAdapterPosition]) }
    }

    override fun getItemCount(): Int {
        return listDevice?.size!!
    }


    interface OnItemClickCallback {
        fun onItemClicked(data: ScanResult)
    }


}