package com.example.birdzone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsActivity : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var editMaxDistance: EditText
    private lateinit var btnSaveSettings: Button
    private lateinit var btnLogOut: Button
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize views
        radioGroup = findViewById(R.id.radioGroup)
        editMaxDistance = findViewById(R.id.editMaxDistance)
        btnSaveSettings = findViewById(R.id.btnSaveSettings)
        btnLogOut = findViewById(R.id.btnLogOut)
        bottomNavigationView = findViewById(R.id.bottomNavigation)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Set the selected item in the bottom navigation view
        bottomNavigationView.selectedItemId = R.id.settings

        // Bottom navigation view item click listener
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Home -> {
                    startActivity(Intent(this, MapActivity::class.java))
                    true
                }
                R.id.observations -> {
                    startActivity(Intent(this, ObservationsActivity::class.java))
                    true
                }
                R.id.savedObservations -> {
                    startActivity(Intent(this, SavedObservationsActivity::class.java))
                    true
                }
                R.id.settings -> true
                else -> false
            }
        }

        // Button click listener for saving settings
        btnSaveSettings.setOnClickListener {
            // Get selected radio button id
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId

            // Get max distance value
            val maxDistance = editMaxDistance.text.toString().toDoubleOrNull()

            // Check if maxDistance is null or invalid, handle it gracefully
            if (maxDistance == null || maxDistance < 0) {
                Toast.makeText(this, "Please enter a valid distance", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Determine the unit based on selected radio button
            val unit = when (selectedRadioButtonId) {
                R.id.radioMetric -> "km" // Metric unit
                R.id.radioImperial -> "mi" // Imperial unit
                else -> "km" // Default to km if none selected
            }

            // Update user settings in Firestore
            val user = auth.currentUser
            user?.let {
                val settings = hashMapOf(
                    "maxDistance" to maxDistance,
                    "unit" to unit // Use "unit" instead of "isMetric" for clarity
                )

                db.collection("users")
                    .document(user.uid)
                    .update("settings", settings)
                    .addOnSuccessListener {
                        // Show a toast or provide feedback to the user
                        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        // Handle any errors that occurred during the update
                        Toast.makeText(this, "Failed to save settings: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        // Button click listener for log out
        btnLogOut.setOnClickListener {
            auth.signOut()

            // Redirect to login activity
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }
}
