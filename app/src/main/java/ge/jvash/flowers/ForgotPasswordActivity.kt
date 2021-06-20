package ge.jvash.flowers

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var forgotToLogin: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var sendLink: Button
    private lateinit var emailText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        mAuth = FirebaseAuth.getInstance()
        getInput()
        registerListeners()
    }

    private fun getInput() {
        sendLink = findViewById(R.id.forgotPasswordSendLinkButton)
        emailText = findViewById(R.id.forgotPasswordInputEmail)
        forgotToLogin = findViewById(R.id.forgotLogin)
    }

    private fun registerListeners() {
        sendLink.setOnClickListener {
            val email = emailText.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this, "Email Field Empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Reset Link was send to your mail!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, "Invalid mail!", Toast.LENGTH_SHORT).show()
                }
            }

        }

        forgotToLogin.setOnClickListener {
            startActivity(
                Intent(
                    this@ForgotPasswordActivity,
                    LoginActivity::class.java
                )
            )
        }

    }
}