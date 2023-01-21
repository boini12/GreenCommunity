package org.wit.greencommunity.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import org.wit.greencommunity.R
import org.wit.greencommunity.databinding.ActivityMainBinding
import org.wit.greencommunity.main.MainApp
import org.wit.greencommunity.models.DistanceModel
import timber.log.Timber.i

/**
 * This is the landing activity
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
    private lateinit var distanceModel : DistanceModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * To get the string from the String resources I used the following link to create the code below in line 66
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

        /**
         * For the implementation of a NumberPicker I followed this guide and changed it a bit:
         * Link: https://tutorialwing.com/android-numberpicker-using-kotlin-with-example/
         * Last accessed: 21.01.2023
         */

        val distanceOptions = arrayOf("5", "10")
        binding.homeActivity.homeNumberPicker.displayedValues = distanceOptions
        binding.homeActivity.homeNumberPicker.minValue = 0
        binding.homeActivity.homeNumberPicker.maxValue = distanceOptions.size - 1

        distanceModel = DistanceModel()

        binding.homeActivity.homeNumberPicker.setOnValueChangedListener() { _, _, newVal ->
            this.distanceModel.chosenDistance = distanceOptions[newVal].toInt()
        }


        i("GreenCommunity Application has been started")

        binding.homeActivity.btnExplore.setOnClickListener(){

            if(auth.currentUser != null){
                val launcherIntent = Intent(this, AdListActivity::class.java)
                launcherIntent.putExtra("distance", this.distanceModel)
                startActivity(launcherIntent)
            }else{
                val launcherIntent = Intent(this@HomeActivity, LoginOrSignUpActivity::class.java)
                startActivity(launcherIntent)
            }
        }
    }
    /**
     * To implement a Navigation Drawer, I used the following guide:
     * Link: https://www.androidhire.com/navigation-drawer-in-kotlin-android/
     * Last accessed: 21.01.2023
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
                    i("User has been logged out")
                    intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this, "No user logged in", Toast.LENGTH_LONG).show()
                }
            }
            R.id.home -> {
                //nothing should happen
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


