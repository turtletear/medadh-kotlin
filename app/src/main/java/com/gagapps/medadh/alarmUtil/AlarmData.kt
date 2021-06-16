package com.gagapps.medadh.alarmUtil

import kotlinx.android.parcel.Parcelize

data class AlarmData (
    var hour: Int,
    var minute: Int,
    var dose: Int,
    var unit: String,
    var medication: String,
    var note: String,
    var reqCode: Int,
    var status : String
)