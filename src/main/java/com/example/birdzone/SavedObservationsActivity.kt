package com.example.birdzone

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.birdzone.Model.Observation
import com.example.birdzone.adapter.ObservationAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SavedObservationsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var observationAdapter: ObservationAdapter
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_observations)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewObservations)
        recyclerView.layoutManager = LinearLayoutManager(this)
        observationAdapter = ObservationAdapter()
        recyclerView.adapter = observationAdapter

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Set the selected item in the bottom navigation view
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.savedObservations

        // Bottom navigation view item click listener
        bottomNavigation.setOnItemSelectedListener { item ->
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
                    true
                }
                R.id.settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Load saved observations
        loadSavedObservations()

    }

    private fun loadSavedObservations() {
        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user?.uid

        if (userUid != null) {
            db.collection("users")
                .document(userUid)
                .collection("observations")
                .get()
                .addOnSuccessListener { result ->
                    val observations = mutableListOf<Observation>()
                    for (document in result) {
                        val species = document.getString("species") ?: ""

                        // Retrieve the date directly as a String
                        val date = document.getString("date") ?: ""  // Use the stored string directly

                        val time = document.getString("time") ?: ""
                        val location = document.getString("location") ?: ""
                        val notes = document.getString("notes") ?: ""

                        // Create Observation object and add it to the list
                        val observation = Observation(species, date, time, location, notes)
                        observations.add(observation)
                    }

                    // Submit the list of observations to the adapter
                    observationAdapter.submitList(observations)
                }
                .addOnFailureListener { exception ->
                    // Handle errors here
                    Toast.makeText(this, "Failed to retrieve observations", Toast.LENGTH_SHORT).show()
                }
        }
    }
}