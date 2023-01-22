package org.wit.greencommunity.models

import android.os.Parcel
import android.os.Parcelable

data class DistanceModel(var chosenDistance: Int = 5) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(chosenDistance)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DistanceModel> {
        override fun createFromParcel(parcel: Parcel): DistanceModel {
            return DistanceModel(parcel)
        }

        override fun newArray(size: Int): Array<DistanceModel?> {
            return arrayOfNulls(size)
        }
    }
}
