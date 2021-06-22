package ge.jvash.flowers

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    private lateinit var role: TextView

    private lateinit var newUsername: TextView
    private lateinit var newPassword: TextView
    private lateinit var newPasswordConfirm: TextView
    private lateinit var currentPassword: TextView
    private lateinit var Uri: String
    private lateinit var userImageView: ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var ImageUri: Uri
    private lateinit var newProductTitle: EditText
    private lateinit var newProductDescription: EditText
    private lateinit var newProductNumber: EditText
    private lateinit var newProductPrice: EditText
    private lateinit var newProductPicture: ImageView

    private var STORAGE_RQ = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavigationDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarNavigationDrawer.toolbar)

        binding.appBarNavigationDrawer.fab.setOnClickListener { view ->

        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawer)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_featured,
                R.id.nav_bucket,
                R.id.nav_my_products,
                R.id.nav_settings,
                R.id.nav_sigout
            ), drawerLayout
        )

        loadProfile()
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawer)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun newProductListeners() {
        findViewById<ImageView>(R.id.newProductImage).setOnClickListener {
            checkForPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                "Storage",
                STORAGE_RQ
            )
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                (startActivityForResult(intent, 100))
            }
        }
        newProductTitle = findViewById(R.id.newProductName)
        newProductDescription = findViewById(R.id.newProductDescription)
        newProductNumber = findViewById(R.id.newProductNumber)
        newProductPrice = findViewById(R.id.newProductPrice)
        newProductPicture = findViewById(R.id.newProductImage)
        findViewById<Button>(R.id.newProductPublish).setOnClickListener {
            if (newProductTitle.text.toString().isEmpty() ||
                newProductDescription.text.toString().isEmpty() ||
                newProductNumber.text.toString().isEmpty() ||
                newProductPrice.text.toString().isEmpty()
            ) {
                Toast.makeText(this, "Fields are empty!", Toast.LENGTH_SHORT).show()
            } else {
                var currDB = FirebaseDatabase.getInstance().getReference("flowers")
                val fileName = System.currentTimeMillis().toString()
                uploadImage(fileName)
            }
        }
    }

    fun checkForPermission(permission: String, name: String, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                }
                shouldShowRequestPermissionRationale(permission) -> showDialog(
                    permission,
                    name,
                    requestCode
                )

                else -> ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    requestCode
                )
            }
        }
    }

    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Permission needed")
            setMessage("This permission is needed for reading images from your device")
            setPositiveButton("OK") { dialog, which ->
                ActivityCompat.requestPermissions(
                    this@NavigationDrawerActivity,
                    arrayOf(permission),
                    requestCode
                )
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
        }

        when (requestCode) {
            STORAGE_RQ -> innerCheck("Storage")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            ImageUri = data?.data!!
            findViewById<ImageView>(R.id.newProductImage)?.setImageURI(ImageUri)
        }
    }

    private fun uploadImage(fileName: String) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading File")
        progressDialog.setCancelable(false)
        progressDialog.show()
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().getReference("flowerImages/$fileName")

        db.setValue(ImageUri).addOnSuccessListener {
            findViewById<ImageView>(R.id.newProductImage).setImageURI(null)
            Toast.makeText(this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()
            if (progressDialog.isShowing) progressDialog.dismiss()
        }.addOnFailureListener {
            if (progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(this, "Something Went Wrong!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProfile() {
        db = FirebaseDatabase.getInstance().getReference("userInfo")
        mAuth = FirebaseAuth.getInstance()
        val uid = mAuth.currentUser?.uid
        if (uid != null) {
            db.child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userInfo: UserInfo = snapshot.getValue(UserInfo::class.java) ?: return
                    role = findViewById(R.id.userRoleProfile)
                    picture = findViewById(R.id.userPicture)
                    email = findViewById(R.id.userMail)
                    username = findViewById(R.id.username)
                    email.text = userInfo.email
                    username.text = userInfo.username
                    role.text = userInfo.role
                    if (role.text.toString() == "EMPLOYEE") {
                        val navigationView: NavigationView = findViewById(R.id.nav_view)
                        val navMenu: Menu = navigationView.menu
                        navMenu.findItem(R.id.nav_my_products).isVisible = false
                        findViewById<FloatingActionButton>(R.id.fab).visibility = View.GONE
                    }
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
        initializeFields()
        val currEmail = findViewById<TextView>(R.id.userMail).text.toString()

        if (newUsername.text.toString().isEmpty())
            Toast.makeText(this, "Username field is empty!", Toast.LENGTH_SHORT).show()
        else {
            val uid = mAuth.currentUser?.uid.toString()
            mAuth.currentUser?.reauthenticate(
                EmailAuthProvider.getCredential(
                    currEmail,
                    currentPassword.text.toString()
                )
            )?.addOnCompleteListener {
                db.child(uid).child("username").setValue(newUsername.text.toString())
                    .addOnCompleteListener {
                        Toast.makeText(this, "Username Updated", Toast.LENGTH_SHORT).show()
                        resetInputFields()
                    }
            }?.addOnFailureListener {
                Toast.makeText(this, "Current Password is incorrect!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun savePassword(view: View) {
        initializeFields()
        val currEmail = findViewById<TextView>(R.id.userMail).text.toString()
        val user = mAuth.currentUser

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
        } else {
            val uid = mAuth.currentUser?.uid.toString()
            user?.reauthenticate(
                EmailAuthProvider.getCredential(
                    currEmail,
                    currentPassword.text.toString()
                )
            )?.addOnCompleteListener {
                user.updatePassword(newPassword.text.toString()).addOnCompleteListener {
                    Toast.makeText(this, "Password Updated Successfully!", Toast.LENGTH_SHORT)
                        .show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Something Went Wrong!", Toast.LENGTH_SHORT).show()
                }
            }?.addOnFailureListener {
                Toast.makeText(this, "Current Password is incorrect!", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun initializeFields() {
        newPassword = findViewById<EditText>(R.id.newPassword)
        newPasswordConfirm = findViewById<EditText>(R.id.newPasswordConfirm)
        currentPassword = findViewById<EditText>(R.id.currentPassword)
        newUsername = findViewById<EditText>(R.id.changeUsername)
    }

    private fun resetInputFields() {
        newPassword.text = ""
        newPasswordConfirm.text = ""
        currentPassword.text = ""
        newUsername.text = ""
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

    fun changeQuantity(view: View) {
        Log.d("Here", "fun")
        val quantityEditText = findViewById<EditText>(R.id.quantityEditText)
        if (view == findViewById(R.id.plusProductButton)) {
            Log.d("Here", "minus")
            quantityEditText.setText((quantityEditText.text.toString().toInt() + 1).toString())
        } else if (view == findViewById(R.id.minusProductButton)) {
            Log.d("Here", "plus")
            if (quantityEditText.text.toString().toInt() > 1)
                quantityEditText.setText((quantityEditText.text.toString().toInt() - 1).toString())
        }
    }

    fun addToCart(view: View) {
        if (view == findViewById(R.id.addToCartButton)) {
            val currDB = FirebaseDatabase.getInstance().getReference("userBucket")

        }
    }

}