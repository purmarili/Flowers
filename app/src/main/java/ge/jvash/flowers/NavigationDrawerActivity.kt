package ge.jvash.flowers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.*
import com.google.firebase.database.*
import ge.jvash.flowers.databinding.ActivityNavigationDrawerBinding

class NavigationDrawerActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityNavigationDrawerBinding
    private lateinit var db: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var picture: ImageView
    private lateinit var email: TextView
    private lateinit var username: TextView

    private lateinit var newUsername: TextView
    private lateinit var newPassword: TextView
    private lateinit var newPasswordConfirm: TextView
    private lateinit var currentPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavigationDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarNavigationDrawer.toolbar)

        binding.appBarNavigationDrawer.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawer)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_featured, R.id.nav_bucket, R.id.nav_my_products
            ), drawerLayout
        )

        loadProfile()
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navigation_drawer, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawer)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun loadProfile() {
        db = FirebaseDatabase.getInstance().getReference("userInfo")
        mAuth = FirebaseAuth.getInstance()
        val uid = mAuth.currentUser?.uid
        if (uid != null) {
            db.child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userInfo: UserInfo = snapshot.getValue(UserInfo::class.java) ?: return
                    picture = findViewById(R.id.userPicture)
                    email = findViewById(R.id.userMail)
                    username = findViewById(R.id.username)
                    email.text = userInfo.email
                    username.text = userInfo.username
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

    }

    fun signOut(item: MenuItem) {
        FirebaseAuth.getInstance().signOut()
        startActivity(
            Intent(
                this,
                LoginActivity::class.java
            )
        )
    }

    fun saveSettings(view: View) {
        newUsername = findViewById<EditText>(R.id.changeUsername)
        currentPassword = findViewById<EditText>(R.id.currentPassword)

        if (newUsername.text.toString().isEmpty())
            Toast.makeText(this, "Username field is empty!", Toast.LENGTH_SHORT).show()
        else {
            if (validateCurrentPassword(currentPassword.text.toString())) {
                val uid = mAuth.currentUser?.uid.toString()
                db.child(uid).child("username").setValue(newUsername).addOnCompleteListener {
                    Toast.makeText(this, "Username Updated", Toast.LENGTH_SHORT).show()
                    resetInputFields()
                }
            } else {
                Toast.makeText(this, "Current password is invalid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun savePassword(view: View) {
        newPassword = findViewById<EditText>(R.id.changePassword)
        newPasswordConfirm = findViewById<EditText>(R.id.changePasswordConfirm)
        currentPassword = findViewById<EditText>(R.id.currentPassword)
        if (newPassword.text.toString().isEmpty() || newPasswordConfirm.text.toString()
                .isEmpty() || currentPassword.text.toString().isEmpty()
        ) {
            Toast.makeText(this, "Fields are empty!", Toast.LENGTH_SHORT).show()
        } else if (!validatePassword(
                newPassword.text.toString(),
                newPasswordConfirm.text.toString()
            )
        ) {
            Toast.makeText(this, "Passwords doesn't match", Toast.LENGTH_SHORT).show()
        } else if (!validateCurrentPassword(currentPassword.text.toString())) {
            Toast.makeText(this, "Current password is invalid", Toast.LENGTH_SHORT).show()
        } else {
            mAuth.currentUser?.updatePassword(newPassword.text.toString())?.addOnCompleteListener {
                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                resetInputFields()
            }?.addOnFailureListener {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetInputFields() {
        newPassword.text = ""
        newPasswordConfirm.text = ""
        currentPassword.text = ""
        newUsername.text = ""
    }

    private fun validateCurrentPassword(password: String): Boolean {
        var isValid = false
        val user = mAuth.currentUser
        user?.reauthenticate(
            EmailAuthProvider.getCredential(
                user.email.toString(),
                password
            )
        )?.addOnSuccessListener {
            isValid = true
        }
        return isValid
    }

    private fun validatePassword(password: String, confirmPassword: String): Boolean {
        if (password != confirmPassword) return false
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

}