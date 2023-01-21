package org.wit.greencommunity.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.squareup.picasso.Picasso
import org.wit.greencommunity.R
import org.wit.greencommunity.adapter.showImagePicker
import org.wit.greencommunity.databinding.ActivitySignUpBinding
import org.wit.greencommunity.main.MainApp
import timber.log.Timber.i

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var app: MainApp
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var img : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appToolbar.toolbar.title = resources.getString(R.string.text_SignUp)
        setSupportActionBar(binding.appToolbar.toolbar)

        img = Uri.EMPTY

        app = application as MainApp

        i("SignUpActivity has started")

            binding.btnSignUp.setOnClickListener(){

                /**
                 * For the signup method I used code from this website:
                 * Link: https://blog.mindorks.com/firebase-login-and-authentication-android-tutorial [section: Register a user with email and password]
                 * Last opened: 29.11.2022
                 * This guide was also used for the [addUsername] and [addUserImgAndUsername] methods
                 */
                if((validateEmail() || validatePassword() || validateUsername()) && validateEmail() && validatePassword() && validateUsername()){
                    auth.createUserWithEmailAndPassword(binding.email.text.toString(), binding.password.text.toString()).addOnCompleteListener(this, OnCompleteListener { task ->
                        if(task.isSuccessful){
                            if(img != null){
                                addUserImgAndUsername(img)
                            }else{
                                addUsername()
                            }
                            Toast.makeText(this, "Successfully Registered", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@SignUpActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else {
                            emailAlreadyUsed()
                        }
                    })
                }
        }
        binding.addImage.setOnClickListener {
            showImagePicker(imageIntentLauncher, this)
        }
        registerImagePickerCallback()

    }

    private fun addUserImgAndUsername(image : Uri){
        if(auth.currentUser != null){
            val profileUpdates = userProfileChangeRequest {
                photoUri = image
                i("photoURI: $photoUri")
                displayName = binding.username.text.toString()
            }

            auth.currentUser!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        i("User has been updated")
                    }
                }
        }
    }

    private fun addUsername(){
        if(auth.currentUser != null){
            val profileUpdates = userProfileChangeRequest {
                displayName = binding.username.text.toString()
            }

            auth.currentUser!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        i("Username has been updated")
                    }
                }
        }
    }

    private fun validateUsername() : Boolean {
        return if(binding.username.text.isEmpty()){
            binding.username.error = "Please enter an username"
            false
        }else{
            true
        }

    }

    private fun validateEmail() : Boolean {
        if(binding.email.text.isEmpty()){
            binding.email.error = "Please enter an email"
            return false
        }
        return true
    }

    private fun validatePassword() : Boolean {
        if(binding.password.text.isEmpty()){
            binding.password.error = "Please enter a password"
            return false
        }else if(binding.password.text.length <= 6){
            binding.password.error = "Enter password with length > 6"
            return false
        }else if((binding.password.text.toString() != binding.passwordAgain.text.toString()) && binding.password.text.isNotEmpty()){
            binding.passwordAgain.error = "The passwords don't match"
            return false
        }
        return true
    }

    private fun emailAlreadyUsed() {
        binding.email.error = "Email already registered"
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
                                .into(binding.profileImg)
                            binding.addImage.setText(R.string.button_addImage)
                        }
                        // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

}