package ge.jvash.flowers

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var signUp: TextView
    private lateinit var forgotPassword: TextView
//    private lateinit var

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        signUp = findViewById(R.id.textViewSignUp)
        forgotPassword = findViewById(R.id.loginForgotPassword)
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
    }

    private fun getInput() {
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}