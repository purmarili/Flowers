package ge.jvash.flowers

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private lateinit var alreadyHaveAccount: TextView
    private lateinit var userNameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var passwordTextView: TextView
    private lateinit var passwordConfirmTextView: TextView
    private lateinit var registerButton: Button
    private lateinit var registerRole: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().getReference("userInfo")

        getInput()
        registerListeners()
    }

    private fun registerListeners() {

        alreadyHaveAccount.setOnClickListener {
            startActivity(
                Intent(
                    this@RegisterActivity,
                    LoginActivity::class.java
                )
            )
        }

        registerButton.setOnClickListener {
            val username = userNameTextView.text.toString()
            val email = emailTextView.text.toString()
            val password = passwordTextView.text.toString()
            val passwordCheck = passwordConfirmTextView.text.toString()
            val role = registerRole.selectedItem.toString()
            if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                Toast.makeText(this, "Some fields are empty", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (!validEmail(email)) {
                Toast.makeText(this, "Email is not valid!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!validPassword(password)) {
                Toast.makeText(
                    this,
                    "Password must be at least 6 size ( letters and digits )",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (password != passwordCheck) {
                Toast.makeText(
                    this,
                    "Passwords doesn't match",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            val user = UserInfo(username, email, "", role.uppercase())
                            db.child(mAuth.currentUser?.uid.toString()).setValue(user)
                            mAuth.signOut()
                            Toast.makeText(
                                this,
                                "Registration Successful, Please log in",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(
                                Intent(
                                    this@RegisterActivity,
                                    LoginActivity::class.java
                                )
                            )
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Registration Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }


    private fun getInput() {
        registerRole = findViewById(R.id.registerRole)
        val roles = listOf("Employee", "Seller")
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, roles)
        registerRole.adapter = adapter

        alreadyHaveAccount = findViewById(R.id.registerAlreadyHaveAccount)
        userNameTextView = findViewById(R.id.registerInputUsername)
        emailTextView = findViewById(R.id.registerInputEmail)
        passwordTextView = findViewById(R.id.changePassword)
        passwordConfirmTextView = findViewById(R.id.changePasswordConfirm)
        registerButton = findViewById(R.id.registerRegister)
    }

    private fun validPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (password.contains(' ')) return false
        var hasDigit = false
        var hasLetter = false
        for (ch in password) {
            if (ch.isDigit()) hasDigit = true
            if (ch.isLetter()) hasLetter = true
        }
        return (hasDigit && hasLetter)
    }

    private fun validEmail(target: CharSequence): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
}