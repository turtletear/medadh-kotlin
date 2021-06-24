package com.gagapps.medadh.practitionerFragment.rvAdapter

import android.app.Activity
import android.os.Build
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gagapps.medadh.R
import com.gagapps.medadh.dataClassMedState.MedStateData
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ListDetailMSAdapter(private val listMedState: List<MedStateData>): RecyclerView.Adapter<ListDetailMSAdapter.ListViewHolder>() {

    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvMedNam: TextView = itemView.findViewById(R.id.tv_detail_med_name)
        val tvMedSch: TextView = itemView.findViewById(R.id.tv_detail_med_schedule)
        val tvMedStat: TextView = itemView.findViewById(R.id.tv_detail_med_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_detail_medstate, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = listMedState[position]
        val actvt = holder.itemView.context as Activity
        holder.tvMedNam.text = data.category.text
        val date = data.effective.effectiveDateTime
        val formattedDate = convDate(date)
        holder.tvMedSch.text = formattedDate
        val stat = data.extension.isLate
        if (stat){
            holder.tvMedStat.text = "Late"
            holder.tvMedStat.setTextColor(ContextCompat.getColor(actvt, R.color.warning_red))
        }
        else{
            holder.tvMedStat.text = "Taken"
            holder.tvMedStat.setTextColor(ContextCompat.getColor(actvt, R.color.lighter_green))
        }

    }

    override fun getItemCount(): Int {
        return listMedState.size
    }

    private fun convDate(date: String): String {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val formatter = SimpleDateFormat("HH:mm")
        val formattedDate = formatter.format(parser.parse(date))

        return formattedDate
    }
}