package com.example.birdzone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.birdzone.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class Login : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvRegister: TextView
    private lateinit var btnLogin: Button

    //firebase auth and firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Initialize views
        etEmail = findViewById(R.id.emailEditText)
        etPassword = findViewById(R.id.passwordEditText)
        tvRegister = findViewById(R.id.registerLinkTextView)
        btnLogin = findViewById(R.id.loginButton)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Set on click listener for button login
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_SHORT).show()
            } else {
                // Sign in with Firebase Authentication
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Login successful
                            val user = auth.currentUser
                            val uid = user?.uid

                            // Fetch additional user information from Firestore
                            uid?.let {
                                db.collection("users")
                                    .document(it)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        if (document.exists()) {
                                            // User data retrieved successfully
                                            val userDocument = document.toObject(User::class.java)

                                            // Now you can access userDocument properties
                                            if (userDocument != null) {
                                                // Access user data, e.g., userDocument.displayName
                                                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                                                // Go to MainActivity
                                                startActivity(Intent(this, MapActivity::class.java))
                                            }
                                        } else {
                                            Toast.makeText(this, "User data not found in Firestore", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(this, "Failed to retrieve user data from Firestore", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            // Login failed
                            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        // Set on click listener for text view register
        tvRegister.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }
    }
}