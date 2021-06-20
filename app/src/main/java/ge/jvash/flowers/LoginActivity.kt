package ge.jvash.flowers

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var signUp: TextView
    private lateinit var forgotPassword: TextView
    private lateinit var loginButton: Button

    private lateinit var emailTextView: TextView
    private lateinit var passwordTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        if (currentUser != null){
            startActivity(Intent(this@LoginActivity, NavigationDrawerActivity::class.java))
            finish()
        }
        getInput()
        registerListeners()

    }

    private fun registerListeners() {
        signUp.setOnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    RegisterActivity::class.java
                )
            )
        }
        forgotPassword.setOnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    ForgotPasswordActivity::class.java
                )
            )
        }
        loginButton.setOnClickListener {
            val email = emailTextView.text.toString()
            val password = passwordTextView.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Some fields are empty", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Login Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(
                            Intent(
                                this@LoginActivity,
                                NavigationDrawerActivity::class.java
                            )
                        )
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Something went wrong, Try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun getInput() {
        signUp = findViewById(R.id.textViewSignUp)
        forgotPassword = findViewById(R.id.loginForgotPassword)
        loginButton = findViewById(R.id.loginLogin)
        emailTextView = findViewById(R.id.loginInputEmail)
        passwordTextView = findViewById(R.id.loginInputPassword)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}