package org.wit.greencommunity.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import org.wit.greencommunity.R
import org.wit.greencommunity.adapter.AdAdapter
import org.wit.greencommunity.adapter.AdListener
import org.wit.greencommunity.databinding.ActivityAdListBinding
import org.wit.greencommunity.main.MainApp
import org.wit.greencommunity.models.AdModel
import org.wit.greencommunity.models.LocationModel
import timber.log.Timber

class AdListActivity : AppCompatActivity(), AdListener, NavigationView.OnNavigationItemSelectedListener{
    
    lateinit var app: MainApp
    private lateinit var binding: ActivityAdListBinding
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navView : NavigationView
    private lateinit var auth : FirebaseAuth
    private lateinit var currentLocation : LocationModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appToolbar.toolbar)
        
        app = application as MainApp

        auth = FirebaseAuth.getInstance()

        val layoutManager = LinearLayoutManager(this)
        binding.adListActivity.recyclerView.layoutManager = GridLayoutManager(applicationContext, 2)
        binding.adListActivity.recyclerView.adapter = AdAdapter(app.ads.findAll(), this)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, binding.appToolbar.toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        //navView.bringToFront()
        navView.setNavigationItemSelectedListener(this)
        
        
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
        launcherIntent.putExtra("ad_edit", ad)
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
                if(auth.currentUser == null){
                    item.isVisible = true
                    intent = Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                }else{
                    item.isVisible = false
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
                TODO("needs to still be implemented")
            }
            R.id.logout -> {
                if(auth.currentUser != null){
                    item.isVisible = true
                    auth.signOut()
                    Toast.makeText(this, "Successfully logged out", Toast.LENGTH_LONG).show()
                    Timber.i("User has been logged out")
                    recreate()
                }else{
                    item.isVisible = false
                }
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
