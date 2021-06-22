package ge.jvash.flowers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class NewProductActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_product)

        findViewById<TextView>(R.id.registerAlreadyHaveAccount).setOnClickListener {
            startActivity(
                Intent(this@NewProductActivity, NavigationDrawerActivity::class.java)
            )
        }
    }


}