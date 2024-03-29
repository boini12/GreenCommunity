package org.wit.greencommunity.testdata

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.wit.greencommunity.models.AdModel

/**
 * The whole purpose of this class is to add some sample ads to the Realtime Database
 * An object of this class used to be implemented in the MainApp and would call [testAdModels]
 */

class FirebaseTestData {

    private lateinit var auth: FirebaseAuth
    private lateinit var database : DatabaseReference
    private var ad = AdModel()

    fun testAdModels(){

        database = FirebaseDatabase.getInstance("https://greencommunity-219d2-default-rtdb.europe-west1.firebasedatabase.app/").getReference("posts")
        auth = FirebaseAuth.getInstance()

        for(i in 1..3){
            val key = database.push().key ?: ""
            ad.id = key
            val newAd = AdModel(ad.id,
                "TestTitle$i",
                "TestDescription$i", 5.0,  -122.04032977923998,   37.39138716107517, false, ad.adImg,
                auth.currentUser?.uid)

            database.child(key)
                .setValue(newAd)
        }
    }
}