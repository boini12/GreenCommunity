package org.wit.greencommunity.models

import timber.log.Timber.i

class AdMemStore: AdStore {


    var ads = ArrayList<AdModel>()

    override fun findAll(): List<AdModel> {
        return ads
    }

    override fun create(ad: AdModel) {
        ads.add(ad)
        logAll()
    }

    override fun update(ad: AdModel) {

    }

    fun logAll(){
        ads.forEach{ i("${it}")}
    }


}