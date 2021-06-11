package com.gagapps.medadh.btUtils

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RxBleParcelable (
    var name: String?,
    var adress: String?
): Parcelable