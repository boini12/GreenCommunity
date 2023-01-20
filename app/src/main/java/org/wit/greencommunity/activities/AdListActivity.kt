package org.wit.greencommunity.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.wit.greencommunity.R
import org.wit.greencommunity.adapter.AdAdapter
import org.wit.greencommunity.adapter.AdListener
import org.wit.greencommunity.databinding.ActivityAdListBinding
import org.wit.greencommunity.main.MainApp
import org.wit.greencommunity.models.AdModel
import timber.log.Timber
import timber.log.Timber.i

class AdListActivity : AppCompatActivity(), AdListener, NavigationView.OnNavigationItemSelectedListener{
    
    lateinit var app: MainApp
    private lateinit var binding: ActivityAdListBinding
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navView : NavigationView
    private lateinit var auth : FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var adList : ArrayList<AdModel>
    private val PERMISSION_ID = 42
    private lateinit var mFusedLocationClient : FusedLocationProviderClient
    private lateinit var location: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        location = Location(LocationManager.GPS_PROVIDER)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

        binding = ActivityAdListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appToolbar.toolbar.title = resources.getString(R.string.adList_title)
        setSupportActionBar(binding.appToolbar.toolbar)
        
        app = application as MainApp

        auth = FirebaseAuth.getInstance()

        binding.adListActivity.recyclerView.layoutManager = GridLayoutManager(applicationContext, 2)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        adList = arrayListOf<AdModel>()
        //realtimeFirebaseData()

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, binding.appToolbar.toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

    }

    override fun onStart() {
        super.onStart()

        realtimeFirebaseData()
    }


    /**
     * For this method I used the following Link
     * This method gets the data from my firebase realtime database and then displays it in the recyclerview using the adapter
     * Link: https://stackoverflow.com/questions/69238874/how-can-i-retrieve-firebase-data-and-implement-it-to-recycler-view
     * Last accessed: 16.01.2023
     */

    private fun realtimeFirebaseData(){
        database = FirebaseDatabase.getInstance("https://greencommunity-219d2-default-rtdb.europe-west1.firebasedatabase.app/").getReference("posts")
        i("Begin: " + this.location)
        database?.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                adList.clear()

                if (snapshot.exists()) {

                    i("Location: " + this@AdListActivity.location)
                    for (list in snapshot.children) {
                        val radiusInMeters = 5000
                        var distance= FloatArray(3)


                        val data = list.getValue(AdModel::class.java)

                        if (data != null) {
                            Location.distanceBetween(this@AdListActivity.location.latitude, this@AdListActivity.location.longitude, data.latitude, data.longitude, distance)
                            if(distance[0] <= radiusInMeters){
                                adList.add(data)
                            }

                        }

                    }

                    binding.adListActivity.recyclerView.adapter = AdAdapter(adList, this@AdListActivity)

                }


            }


            override fun onCancelled(error: DatabaseError) {
                i("Data couldn't be received")
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, AdActivity::class.java)
                getResult.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                (binding.adListActivity.recyclerView.adapter)?.
                notifyItemRangeChanged(0,app.ads.findAll().size)
            }
        }

    override fun onAdClick(ad: AdModel) {

        val launcherIntent = Intent(this, AdActivity::class.java)
        launcherIntent.putExtra("ad_view", ad)
        getClickResult.launch(launcherIntent)

    }

    private val getClickResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == Activity.RESULT_OK){
            (binding.adListActivity.recyclerView.adapter)?.
            notifyItemRangeChanged(0, app.ads.findAll().size)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.login -> {
                if(auth.currentUser != null){
                    // nothing happens
                }else{
                    intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }

            }
            R.id.profile -> {
                if(auth.currentUser != null){
                    intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this, "You need to log in in order to see your profile", Toast.LENGTH_LONG).show()
                    intent = Intent(this, LoginOrSignUpActivity::class.java)
                    startActivity(intent)
                }
            }
            R.id.ads -> {
                if(auth.currentUser != null){
                    intent = Intent(this, UserAdsActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this, "You need to log in in order to see your ads", Toast.LENGTH_LONG).show()
                    intent = Intent(this, LoginOrSignUpActivity::class.java)
                    startActivity(intent)
                }
            }
            R.id.logout -> {
                if(auth.currentUser != null){
                    auth.signOut()
                    Toast.makeText(this, "Successfully logged out", Toast.LENGTH_LONG).show()
                    Timber.i("User has been logged out")
                    intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
            }
            R.id.home -> {
                intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * For all the following methods I used the following guide to get the current Location of the user
     * This is needed in order to display the correct ads that are active near this user
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
                        Toast.makeText(this, "Location could not be received", Toast.LENGTH_LONG).show()
                    }else{
                        this.location = location
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
