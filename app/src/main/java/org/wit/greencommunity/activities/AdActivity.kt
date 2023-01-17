package org.wit.greencommunity.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.awaitAll
import org.wit.greencommunity.R
import org.wit.greencommunity.adapter.showImagePicker
import org.wit.greencommunity.databinding.ActivityAdBinding
import org.wit.greencommunity.databinding.ActivityAdViewBinding
import org.wit.greencommunity.main.MainApp
import org.wit.greencommunity.models.AdModel
import org.wit.greencommunity.models.LocationModel
import timber.log.Timber.i
import java.net.URI

/**
 *  This is the AdActivity of the GreenCommunity App
 *  this activity is used to add a new activity to the app and also add it to a users account
 *  [writeNewAd] pushes the new ad with its relevant information to the realtime database from firbase
 */

class AdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdBinding
    private lateinit var viewBinding:ActivityAdViewBinding
    lateinit var app: MainApp
    private var ad = AdModel()
    private lateinit var auth: FirebaseAuth
    private lateinit var database : DatabaseReference
    private val PERMISSION_ID = 42
    private lateinit var mFusedLocationClient : FusedLocationProviderClient
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var img : Uri
    var edit = false
    private lateinit var key : String

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


        img = if(ad.adImg != null){
            ad.adImg!!.toUri()
        }else{
            Uri.EMPTY
        }

        key = if(ad.id != null){
            ad.id.toString()
        }else{
            ""
        }


        i("AdActivity has started")

        if(intent.hasExtra("ad_view")){
            ad = intent.extras?.getParcelable("ad_view")!!
            viewBinding = ActivityAdViewBinding.inflate(layoutInflater)
            setContentView(viewBinding.root)
            viewBinding.adTitle.text = ad.title
            viewBinding.adDescription.text = ad.description
            viewBinding.adFree.isChecked = ad.isFree
            viewBinding.adPrice.text = ad.price.toString()
            Picasso.get()
                .load(ad.adImg?.toUri())
                .placeholder(R.mipmap.ic_launcher)
                .into(viewBinding.adViewImg)
        }



        if(intent.hasExtra("ad_edit")) {
            edit = true
            ad = intent.extras?.getParcelable("ad_edit")!!
            binding.adTitle.setText(ad.title)
            binding.btnAddImg.text = "Change Image"
            binding.adDescription.setText(ad.description)
            binding.adFree.isChecked = ad.isFree
            binding.adPrice.setText(ad.price.toString())
            binding.btnAdd.text = "Save Ad"
            Picasso.get()
                .load(ad.adImg?.toUri())
                .placeholder(R.mipmap.ic_launcher)
                .into(binding.adImg)

            img = ad.adImg?.toUri() ?: Uri.EMPTY
        }

        binding.btnAddImg.setOnClickListener {
            showImagePicker(imageIntentLauncher, this)
        }
        registerImagePickerCallback()

        // to disable the EditText concerning the price

        binding.adFree.setOnClickListener {
            if(binding.adFree.isChecked){
                binding.adPrice.isEnabled = false
                binding.adPrice.setText("0.0")
            }else{
                binding.adPrice.isEnabled = true
            }
        }

        binding.btnAdd.setOnClickListener{
            i("ID is: $ad.id")
            ad.title = binding.adTitle.text.toString()
            ad.description = binding.adDescription.text.toString()
            ad.isFree = binding.adFree.isChecked
            ad.price = binding.adPrice.text.toString().toDouble()
            ad.adImg = img.toString()

            if(ad.title!!.isNotEmpty()){

                if(edit){
                    updateAd()
                    setResult(RESULT_OK)
                    finish()
                }else{
                    writeNewAd()
                    setResult(RESULT_OK)
                    finish()
                }
            }else{
                Snackbar.make(it, "Please Enter a Title", Snackbar.LENGTH_LONG).show()
            }

        }
    }

    private fun registerImagePickerCallback(){
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")

                            val image = result.data!!.data!!
                            i("image: $image")
                            img = image
                            i("img: $img")
                            contentResolver.takePersistableUriPermission(image,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            Picasso.get()
                                .load(image)
                                .into(binding.adImg)
                        }
                        // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    /**
     * For this method I used a changed version of a method shown in this link
     * Link: https://www.kodeco.com/books/saving-data-on-android/v1.0/chapters/13-reading-to-writing-from-realtime-database
     * Last opened: 16.01.2023
     */

    private fun writeNewAd(){
        key = database.push().key ?: ""
        ad.id = key
        var newAd = AdModel(ad.id, ad.title, ad.description, ad.price, ad.longitude, ad.latitude, ad.isFree, ad.adImg,
                    auth.currentUser?.uid)

        database.child(key)
            .setValue(newAd)

    }

    private fun updateAd(){
        val updatedAd = AdModel(ad.id, ad.title, ad.description, ad.price, ad.longitude, ad.latitude, ad.isFree, ad.adImg,
                        auth.currentUser?.uid)

        key = ad.id.toString()
        i(ad.adImg)

        database.child(key).setValue(updatedAd)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_ad, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_delete -> {
                deleteAd()
                setResult(RESULT_OK)
                finish()
                val intent = Intent(this, AdListActivity::class.java)
                startActivity(intent)
            }
            R.id.item_cancel -> {
                setResult(RESULT_OK)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if(edit){
            if (menu != null) {
                menu.findItem(R.id.item_delete).isVisible = true
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }


private fun deleteAd() {
        database.child(key).removeValue()
    }

}
