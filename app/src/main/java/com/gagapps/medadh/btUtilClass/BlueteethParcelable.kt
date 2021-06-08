package com.gagapps.medadh.btUtilClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BlueteethParcelable (
            var deviceName: String,
            var deviceAddress: String
        ) : Parcelable