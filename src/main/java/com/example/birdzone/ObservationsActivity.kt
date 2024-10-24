package com.example.birdzone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.birdzone.Model.Observation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ObservationsActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    private lateinit var editTextSpecies: EditText
    private lateinit var editTextLocation: EditText
    private lateinit var editTextNotes: EditText
    private lateinit var textViewDate: TextView
    private lateinit var textViewTime: TextView
    private lateinit var buttonSaveObservation: Button
    private lateinit var bottomNavigation: BottomNavigationView



    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_observations)

        // Initialize views
        editTextSpecies = findViewById(R.id.editTextSpecies)
        editTextLocation = findViewById(R.id.editTextLocation)
        editTextNotes = findViewById(R.id.editTextNotes)
        textViewDate = findViewById(R.id.editTextDate)
        textViewTime = findViewById(R.id.editTextTime)
        buttonSaveObservation = findViewById(R.id.buttonSave)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Set the selected item in the bottom navigation view
        bottomNavigation.selectedItemId = R.id.observations

        // Bottom navigation view item click listener
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Home -> {
                    startActivity(Intent(this, MapActivity::class.java))
                    true
                }
                R.id.observations -> {
                    true
                }
                R.id.savedObservations -> {
                    startActivity(Intent(this, SavedObservationsActivity::class.java))
                    true
                }
                R.id.settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check and request location permission
        if (checkLocationPermission()) {
            fetchLastLocation()
        }

        // Display the current date and time
        displayCurrentDateTime()

        // Save observation to Firestore
        buttonSaveObservation.setOnClickListener {
            saveObservationToFirestore()
        }
    }

    // Display current date and time
    private fun displayCurrentDateTime() {
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        textViewDate.text = currentDate
        textViewTime.text = currentTime
    }

    // Check location permission
    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return false
        }
        return true
    }

    // Fetch last known location
    private fun fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                currentLocation = it
                val latitude = it.latitude
                val longitude = it.longitude
                editTextLocation.setText("$latitude, $longitude")
            }
        }
    }

    // Save observation to Firestore
    private fun saveObservationToFirestore() {
        val species = editTextSpecies.text.toString()
        val date = textViewDate.text.toString()
        val time = textViewTime.text.toString()
        val location = editTextLocation.text.toString()
        val notes = editTextNotes.text.toString()

        if (species.isNotEmpty() && location.isNotEmpty()) {
            val observation = Observation(species, date, time, location, notes)

            // Get the current user's UID from Firebase Authentication
            val user = auth.currentUser
            val userUid = user?.uid

            if (userUid != null) {
                // Reference to the user's document in the "users" collection
                val userDocRef = db.collection("users").document(userUid)

                // Reference to the "observations" subcollection inside the user's document
                val observationsCollectionRef = userDocRef.collection("observations")

                // Add the observation
                observationsCollectionRef.add(observation)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(
                            this,
                            "Observation added with ID: ${documentReference.id}",
                            Toast.LENGTH_SHORT
                        ).show()

                        // After successfully saving, navigate to SavedObservationsActivity
                        val intent = Intent(this, SavedObservationsActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error adding observation: $e", Toast.LENGTH_SHORT)
                            .show()
                    }
            } else {
                Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Species and Location fields are required.", Toast.LENGTH_SHORT)
                .show()
        }
    }
}
