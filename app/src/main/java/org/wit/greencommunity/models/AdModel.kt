package org.wit.greencommunity.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * Data class that represents an ad that can be posted by an user
 * @param id = Id of the ad
 * @param title = title that can be specified by the user
 * @param description = description that can be written by the user
 * @param price = number that the user can use
 * @param isFree = if true then price = 0 otherwise price will be > 0
 */

@Parcelize
data class AdModel(var id: Long = 0L,
                   var title:String="",
                   var description:String="",
                   var price:Double=0.00,
                   var longitude:Double = 0.00,
                   var latitude:Double = 0.00,
                   var isFree:Boolean=false) : Parcelable

