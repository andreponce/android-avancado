package com.ponce.cesarschool.braziliancities.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class City(
    var name :String,
    var image :String,
    var days :String,
    var price :String
) : Parcelable