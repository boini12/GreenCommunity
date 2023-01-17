package org.wit.greencommunity.models

import android.net.Uri
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
data class AdModel(var id: String?= null,
                   var title:String?= null,
                   var description:String?=null,
                   var price:Double=0.00,
                   var longitude:Double = 0.00,
                   var latitude:Double = 0.00,
                   var isFree:Boolean=false,
                   var adImg: String? = null,
                   var userID: String?=null) : Parcelable

