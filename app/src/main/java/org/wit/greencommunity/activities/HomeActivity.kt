package org.wit.greencommunity.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.Person
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import org.wit.greencommunity.R
import org.wit.greencommunity.adapter.adjustNavHeader
import org.wit.greencommunity.databinding.ActivityMainBinding
import org.wit.greencommunity.main.MainApp
import org.wit.greencommunity.models.LocationModel
import org.wit.greencommunity.models.UserModel
import timber.log.Timber
import timber.log.Timber.i
import java.util.jar.Manifest


/**
 * The main activity of the GreenCommunity application
 * From here the user can explore their area through a button
 * User will get redirected if no currentUser is detected -> Redirected to LoginOrSignUpActivity
 * To navigate through the possible views a Navigation Drawer is implemented
 */

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    lateinit var app : MainApp
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * To get the string from the String resources I used the following link to create the method below in line 66
         * Link: https://stackoverflow.com/questions/7493287/android-how-do-i-get-string-from-resources-using-its-name
         * Last accessed: 20.01.2023
         */
        binding.appToolbar.toolbar.title = resources.getString(R.string.app_name)
        setSupportActionBar(binding.appToolbar.toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, binding.appToolbar.toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.bringToFront()
        navView.setNavigationItemSelectedListener(this)

        auth = FirebaseAuth.getInstance()

        app = application as MainApp

        i("GreenCommunity Application has been started")

        binding.homeActivity.btnExplore.setOnClickListener(){
            i("Explore Button pressed")


            intent = if(auth.currentUser != null){
                Intent(this@HomeActivity, AdListActivity::class.java)
            }else{
                Intent(this@HomeActivity, LoginOrSignUpActivity::class.java)
            }

            startActivity(intent)
        }
    }

    /*
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home,menu)
        if(auth.currentUser != null && menu != null){
            menu.getItem(0).isVisible = true
            menu.getItem(1).isVisible = true
            menu.getItem(2).isVisible = true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_logout -> {
                auth.signOut()
                finish()
                i("User has been logged out")
                recreate()

            }

            R.id.item_profile -> {
                intent = Intent(this@HomeActivity, ProfileActivity::class.java)
                startActivity(intent)
            }

            R.id.item_add -> {
                intent = Intent(this@HomeActivity, AdActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }


     */





    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.login -> {
                intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
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
                }else{
                    Toast.makeText(this, "No user logged in", Toast.LENGTH_LONG).show()
                }
            }
            R.id.home -> {
                intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
            R.id.signup -> {
                intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}


