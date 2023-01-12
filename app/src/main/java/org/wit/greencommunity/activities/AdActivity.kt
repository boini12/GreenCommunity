package org.wit.greencommunity.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.awaitAll
import org.wit.greencommunity.R
import org.wit.greencommunity.databinding.ActivityAdBinding
import org.wit.greencommunity.main.MainApp
import org.wit.greencommunity.models.AdModel
import org.wit.greencommunity.models.LocationModel
import timber.log.Timber.i

/**
 *  This is the AdActivity of the GreenCommunity App
 *  this activity is used to add a new activity to the app and also add it to a users account
 *  [writeNewAd] pushes the new ad with its relevant information to the realtime database from firbase
 */

class AdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdBinding
    lateinit var app: MainApp
    private var ad = AdModel()
    private lateinit var auth: FirebaseAuth
    private lateinit var database : DatabaseReference
    private val PERMISSION_ID = 42
    private lateinit var mFusedLocationClient : FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLastLocation()

        app = application as MainApp

        database = FirebaseDatabase.getInstance("https://greencommunity-219d2-default-rtdb.europe-west1.firebasedatabase.app/").getReference("posts")
        auth = FirebaseAuth.getInstance()


        i("AdActivity has started")

        if(intent.hasExtra("ad_edit")) {
            ad = intent.extras?.getParcelable("ad_edit")!!
            binding.adTitle.setText(ad.title)
            binding.adDescription.setText(ad.description)
            binding.adFree.isChecked = ad.isFree
            binding.adFree.text = ad.price.toString()
        }

        binding.btnAdd.setOnClickListener{
            ad.id = app.ads.getId()
            i("ID is: $ad.id")
            ad.title = binding.adTitle.text.toString()
            ad.description = binding.adDescription.text.toString()

            binding.adFree.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked){
                    Toast.makeText(this, isChecked.toString(), Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this, isChecked.toString(), Toast.LENGTH_LONG).show()
                }
            }

            ad.isFree = binding.adFree.isChecked
            if(ad.isFree){
                ad.price = 0.0
            }else{
                ad.price = binding.adPrice.text.toString().toDouble()
            }

            if(ad.title.isNotEmpty()){
                writeNewAd()
                app.ads.create(ad.copy())
                setResult(RESULT_OK)
                finish()
            }else{
                Snackbar.make(it, "Please Enter a Title", Snackbar.LENGTH_LONG).show()
            }

        }
    }

    /**
     * For this method I used a changed version of a method shown in this link
     * Link: https://www.folkstalk.com/tech/how-to-prevent-overwriting-existing-data-in-firebase-with-solutions/
     * Last opened: 05.12.2022
     */

    private fun writeNewAd(){
        var newAd = AdModel(ad.id, ad.title, ad.description, ad.price, ad.longitude, ad.latitude, ad.isFree)
        database.child(newAd.id.toString()).push().setValue(newAd)
        database.child(newAd.id.toString()).child("user").setValue(auth.currentUser!!.uid)
    }

    /**
     * For all the following methods I used the following guide to get the current Location of the user
     * This is needed, in order to save the location for each new ad
     * Link: https://www.androidhire.com/current-location-in-android-using-kotlin/
     * Last accessed: 12.01.2023
     */

    private fun checkPermissions(): Boolean {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun requestPermissions(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_ID){
            if((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                //Granted
            }
        }
    }

    private fun isLocationEnabled() : Boolean {
        var locationManager : LocationManager = getSystemService(Context.LOCATION_SERVICE) as
                LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun getLastLocation(){
        if(checkPermissions()){
            if(isLocationEnabled()){

                if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if(location == null){
                        Toast.makeText(this, "sth. went wrong", Toast.LENGTH_LONG).show()
                    }else{
                        //Toast.makeText(this, location.latitude.toString(), Toast.LENGTH_LONG).show()
                        //Toast.makeText(this, location.longitude.toString(), Toast.LENGTH_LONG).show()
                        ad.longitude = location.longitude
                        ad.latitude = location.latitude
                    }
                }
            }else{
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }

        }else{
            requestPermissions()
        }
    }

}
