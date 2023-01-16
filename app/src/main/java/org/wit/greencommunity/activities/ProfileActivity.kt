package org.wit.greencommunity.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.squareup.picasso.Picasso
import org.wit.greencommunity.R
import org.wit.greencommunity.adapter.adjustNavHeader
import org.wit.greencommunity.adapter.showImagePicker
import org.wit.greencommunity.databinding.ActivityProfileBinding
import org.wit.greencommunity.main.MainApp
import org.wit.greencommunity.models.UserModel
import timber.log.Timber
import timber.log.Timber.i

/**
 * This is the ProfileActivity of the GreenCommunity Application
 * Here a user can see all his current information that is saved in Firebase
 * A user has the chance to make changes to his/her profile and those changes are updated in Firebase
 */

class ProfileActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var binding: ActivityProfileBinding
    lateinit var app: MainApp
    private lateinit var auth : FirebaseAuth
    private lateinit var user : FirebaseUser
    private lateinit var userModel : UserModel
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var img : Uri
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navView : NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appToolbar.toolbar)

        Timber.i("ProfileActivity has started")

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, binding.appToolbar.toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.bringToFront()
        navView.setNavigationItemSelectedListener(this)


        img = if(user.photoUrl != null){
            user.photoUrl!!
        }else{
            Uri.EMPTY

        }

        app = application as MainApp

        user.let {
            binding.profileActivity.username.setText(user.displayName)
            binding.profileActivity.email.setText(user.email)

        }
        Picasso.get()
            .load(user.photoUrl)
            .into(binding.profileActivity.profileImg)

        userModel = UserModel(binding.profileActivity.username.text.toString(), binding.profileActivity.email.text.toString(), "", null)

        binding.profileActivity.username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                userModel.username = binding.profileActivity.username.text.toString()
            }

        })

        binding.profileActivity.btnChangeImg.setOnClickListener {
            showImagePicker(imageIntentLauncher, this)
        }
        registerImagePickerCallback()

        binding.profileActivity.btnChange.setOnClickListener{


            val profileUpdates = userProfileChangeRequest {
                photoUri = img
                displayName = binding.profileActivity.username.text.toString()
            }

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        Timber.i("User has been updated")
                    }
                }
        }

    }

    /*
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.item_home ->{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

     */



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
                                .into(binding.profileActivity.profileImg)
                        }
                        // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.home -> {
                intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }

            R.id.login -> {
                if(auth.currentUser == null){
                    item.isVisible = true
                    intent = Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                }else{
                    item.isVisible = false
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
                    item.isVisible = true
                    auth.signOut()
                    Toast.makeText(this, "Successfully logged out", Toast.LENGTH_LONG).show()
                    i("User has been logged out")
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