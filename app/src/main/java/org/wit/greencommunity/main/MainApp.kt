package org.wit.greencommunity.main

import android.app.Application
import com.github.ajalt.timberkt.Timber
import org.wit.greencommunity.testdata.FirebaseTestData
import timber.log.Timber.i


class MainApp : Application() {

    override fun onCreate(){
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("GreenCommunity started")
    }

}