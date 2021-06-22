package ge.jvash.flowers.ui.settings

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ge.jvash.flowers.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private var _binding: FragmentSettingsBinding? = null
    private var STORAGE_RQ = 101

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var ImageUri: Uri
    private lateinit var db: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val intent = Intent()
        intent.type = "images/*"
        intent.action = Intent.ACTION_GET_CONTENT

        _binding!!.changeProfilePicture.setOnClickListener {
            checkForPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                "Storage",
                STORAGE_RQ
            )
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                (startActivityForResult(intent, 100))
            }
        }
        _binding!!.saveNewPicture.setOnClickListener {
            uploadImage()
        }
        settingsViewModel.text.observe(viewLifecycleOwner,
            {
            })
        return root
    }

    fun checkForPermission(permission: String, name: String, requestCode: Int) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            shouldShowRequestPermissionRationale(permission) -> showDialog(
                permission,
                name,
                requestCode
            )

            else -> ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(permission),
                requestCode
            )
        }
    }

    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(activity)
        builder.apply {
            setTitle("Permission needed")
            setMessage("This permission is needed for reading images from your device")
            setPositiveButton("OK") { dialog, which ->
                ActivityCompat.requestPermissions(
                    requireActivity(),
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
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
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
            _binding?.changeProfilePicture?.setImageURI(ImageUri)
        }
    }

    private fun uploadImage() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Uploading File")
        progressDialog.setCancelable(false)
        progressDialog.show()
        mAuth = FirebaseAuth.getInstance()
        val fileName = mAuth.currentUser?.uid
        db = FirebaseDatabase.getInstance().getReference("profileImages/$fileName")
        if (Uri.EMPTY.equals(ImageUri)) {
            Toast.makeText(context, "No Image Selected", Toast.LENGTH_SHORT).show()
        } else {
            db.setValue(ImageUri).addOnSuccessListener {
                Toast.makeText(context, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()
                if (progressDialog.isShowing) progressDialog.dismiss()
            }.addOnFailureListener {
                if (progressDialog.isShowing) progressDialog.dismiss()
                Toast.makeText(context, "Something Went Wrong!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}