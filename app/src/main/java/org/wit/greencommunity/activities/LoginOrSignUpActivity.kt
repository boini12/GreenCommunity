package org.wit.greencommunity.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import org.wit.greencommunity.databinding.ActivitiyLoginOrSignUpBinding
import org.wit.greencommunity.main.MainApp
import timber.log.Timber

/**
 * This activity becomes active, when the user has pressed the "Explore your area button" from the HomeActivity and currentUser == null
 * In this activity there are two buttons implemented. Each one will redirect the user to the preferred option. Either login or sign-up.
 */

class LoginOrSignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitiyLoginOrSignUpBinding
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding = ActivitiyLoginOrSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        Timber.i("LoginActivity has started")

        binding.btnSignUp.setOnClickListener{
            val intent = Intent(this@LoginOrSignUpActivity, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnLogin.setOnClickListener{
            val intent = Intent(this@LoginOrSignUpActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}