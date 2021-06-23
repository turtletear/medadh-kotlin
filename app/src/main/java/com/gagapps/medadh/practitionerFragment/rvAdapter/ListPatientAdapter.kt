package com.gagapps.medadh.practitionerFragment.rvAdapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gagapps.medadh.R
import com.gagapps.medadh.RetrofitInstance
import com.gagapps.medadh.dataClassMedState.MedStateDC
import com.gagapps.medadh.dataClassPatient.dataClassMultiPatient.Data
import com.gagapps.medadh.interfaces.PatientsServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListPatientAdapter(private val listPatient: List<Data>): RecyclerView.Adapter<ListPatientAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback
    interface OnItemClickCallback {
        fun onItemClicked(data: Data)
    }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tv_patient_name)
        var tvEmail: TextView = itemView.findViewById(R.id.tv_patient_des)
        var tvAdh: TextView = itemView.findViewById(R.id.tv_patient_adh)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_prac_home, parent, false)

        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = listPatient[position]
        val patToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjYwY2NmN2ZhZDg0NWZiMDAwNDE4MWRhZCIsInJvbGUiOiJQYXRpZW50IiwiaWF0IjoxNjI0NDUwNDg4LCJleHAiOjE2MjcwNDI0ODh9.AxOoy_m_T9thjJJpFHuA3hcSUsDOy2SunprT3VcRrbU"
        val actvt = holder.itemView.context as Activity
        holder.tvName.text = data.name.given
        holder.tvEmail.text = data.telecom.value
        getMedStateData(data._id, patToken, holder.tvAdh, actvt)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listPatient[holder.adapterPosition])
        }

    }

    override fun getItemCount(): Int {
        return listPatient.size
    }

    private fun getMedStateData(patientId: String?, token: String?, tvAdh: TextView, actvt: Activity){
        var msData: MedStateDC? = null
        val restService: PatientsServices = RetrofitInstance
            .getRetrofitInstance()
            .create(PatientsServices::class.java)
        val reqCall = restService.getAllPatientMedState(patientId, "Bearer "+token)
        reqCall.enqueue(object : Callback<MedStateDC> {
            override fun onResponse(call: Call<MedStateDC>, response: Response<MedStateDC>) {
                if(response.message() == "OK"){
                    val data = response.body()
                    if(data!=null){
                        val msData = data
                        val ttlPercent = getTotalMSPercentage(msData)
                        val tpConvert = ttlPercent.toInt()
                        if(tpConvert >70){
                            tvAdh.setTextColor(ContextCompat.getColor(actvt, R.color.lighter_green))
                        }
                        else if (tpConvert >=50 && tpConvert <= 70){
                            tvAdh.setTextColor(ContextCompat.getColor(actvt, R.color.grey))
                        }
                        else{
                            tvAdh.setTextColor(ContextCompat.getColor(actvt, R.color.warning_red))
                        }
                        tvAdh.text = ttlPercent +"%"
                    }
                }
                else
                    Log.d("medAdh", "Fail to load patient Medication Statement data")
            }

            override fun onFailure(call: Call<MedStateDC>, t: Throwable) {
                t.printStackTrace()
                Log.d("medAdh", "<ON FAILURE> Fail to load patient Medication Statement data")
            }

        })
    }

    private fun getTotalMSPercentage(medState_data: MedStateDC): String {
        var goodAdh: Float
        val medStatesList = medState_data.data
        var listOfZero = arrayListOf<Float>()
        var listOfOne = arrayListOf<Float>()
        var totalSize = medStatesList?.size
        if (medStatesList != null) {
            var itr = 0
            for (i in medStatesList){
                itr++
                val adhrncBool = i.extension.isLate
                if (adhrncBool)
                    listOfOne.add(1F)
                else
                    listOfZero.add(0F)
            }//end for
        }//end if
        goodAdh = listOfOne.size.toFloat() / totalSize.toFloat() * 100
        return String.format("%.0f", goodAdh)
    }
}