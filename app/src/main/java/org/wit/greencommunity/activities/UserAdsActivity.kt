package org.wit.greencommunity.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
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

class UserAdsActivity : AppCompatActivity(), AdListener, NavigationView.OnNavigationItemSelectedListener{
    lateinit var app: MainApp
    private lateinit var binding: ActivityAdListBinding
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navView : NavigationView
    private lateinit var auth : FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var userAdList : ArrayList<AdModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appToolbar.toolbar.title = resources.getString(R.string.title_userAds)
        setSupportActionBar(binding.appToolbar.toolbar)

        app = application as MainApp

        auth = FirebaseAuth.getInstance()

        binding.adListActivity.recyclerView.layoutManager = GridLayoutManager(applicationContext, 2)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        userAdList = arrayListOf()

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
     *
     * The difference from the method in the AdListActivity, is that here only the ads that belong to the currently
     * logged in user, will get displayed in the RecyclerView
     */

    private fun realtimeFirebaseData(){
        database = FirebaseDatabase.getInstance("https://greencommunity-219d2-default-rtdb.europe-west1.firebasedatabase.app/").getReference("posts")
        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                userAdList.clear()

                if (snapshot.exists()) {

                    for (list in snapshot.children) {
                        val data = list.getValue(AdModel::class.java)
                        if (data != null && auth.currentUser != null) {
                            if(data.userID == auth.currentUser!!.uid){
                                userAdList.add(data)
                            }
                        }
                    }
                    binding.adListActivity.recyclerView.adapter = AdAdapter(userAdList, this@UserAdsActivity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.i("Data couldn't be received")
            }

        })
    }

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
                // nothing should happen
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

    override fun onAdClick(ad: AdModel) {
        val launcherIntent = Intent(this, AdActivity::class.java)
        launcherIntent.putExtra("ad_edit", ad)
        startActivity(launcherIntent)
    }

}