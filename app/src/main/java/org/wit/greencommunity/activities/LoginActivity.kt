package org.wit.greencommunity.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import org.wit.greencommunity.R
import org.wit.greencommunity.databinding.ActivityLoginBinding
import org.wit.greencommunity.main.MainApp
import timber.log.Timber

/**
 * Here the user can enter an email and a password to login
 * the credentials will be compared to the firebase database
 * if correctly entered then the user will be logged in and redirected to the HomeActivity otherwise an error message via a TextView will be displayed
 */

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appToolbar.toolbar.title = resources.getString(R.string.text_login)
        setSupportActionBar(binding.appToolbar.toolbar)

        app = application as MainApp

        Timber.i("SignUpActivity has started")

        binding.btnLogin.setOnClickListener {

            /**
             * For the login method I used code from this website:
             * Link: https://blog.mindorks.com/firebase-login-and-authentication-android-tutorial [section: Login a user with email and password]
             * Last opened: 29.11.2022
             */

            if((validateEmail() || validatePassword()) && validatePassword() && validateEmail()){

                auth.signInWithEmailAndPassword(binding.email.text.toString(), binding.password.text.toString()).addOnCompleteListener(this
                ) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        binding.errorText.text = resources.getString(R.string.error_text)
                        binding.errorText.visibility = View.VISIBLE
                    }
                }
            }

        }

        /**
         * To reset a password of an account, I used the following guide:
         * Link: https://blog.mindorks.com/firebase-login-and-authentication-android-tutorial [section: Send the change password link to email]
         * Last opened: 29.11.2022
         */

        binding.btnForgotPassword.setOnClickListener {

            if(validateEmail()){
                auth.sendPasswordResetEmail(binding.email.text.toString()).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this, "Unable to send reset mail", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun validateEmail() : Boolean {
        if(binding.email.text.isEmpty()){
            binding.email.error = resources.getString(R.string.enter_email)
            return false
        }
        return true
    }

    private fun validatePassword() : Boolean {
        if(binding.password.text.isEmpty()){
            binding.password.error = resources.getString(R.string.enter_password)
            return false
        }
        return true
    }

}