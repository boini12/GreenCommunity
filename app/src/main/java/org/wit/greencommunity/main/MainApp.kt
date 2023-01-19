package org.wit.greencommunity.main

import android.app.Application
import com.github.ajalt.timberkt.Timber
import org.wit.greencommunity.models.AdMemStore
import org.wit.greencommunity.models.AdModel
import org.wit.greencommunity.testdata.FirebaseTestData
import timber.log.Timber.i


class MainApp : Application() {

    val ads = AdMemStore()

    override fun onCreate(){
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("GreenCommunity started")

/*
        var test = FirebaseTestData()

        test.testAdModels()



 */

    }

}