package com.gagapps.medadh.btUtil.rvAdapter

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gagapps.medadh.R

class DeviceListAdapter(private val listDevice: ArrayList<BluetoothDevice>): RecyclerView.Adapter<DeviceListAdapter.ListViewHolder>() {
    lateinit var onItemClickCallback: OnItemClickCallback
    interface OnItemClickCallback {
        fun onItemClicked(data: BluetoothDevice)
    }
    @JvmName("setOnItemClickCallback1")
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvDevName: TextView = itemView.findViewById(R.id.tv_device_name)
        var tvDevAdd: TextView = itemView.findViewById(R.id.tv_device_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_device, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = listDevice.get(position)

        holder.tvDevName.text = data.name
        holder.tvDevAdd.text = data.address

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listDevice[holder.bindingAdapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return listDevice.size
    }
}