package com.example.birdzone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvLogin: TextView
    private lateinit var btnRegister: Button

    //firebase auth and firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        //initialize views
        etEmail = findViewById(R.id.emailEditText)
        etPassword = findViewById(R.id.passwordEditText)
        tvLogin = findViewById(R.id.loginLinkTextView)
        btnRegister = findViewById(R.id.registerButton)

        //initialize firebase auth and firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        //set on click listener for button register
        btnRegister.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            // Ensure that email and password are not empty
            if (email.isEmpty() || password.isEmpty()) {
                // Handle empty fields, display an error message, etc.
                Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_SHORT).show()
            } else {
                // Create a new user with Firebase Authentication
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Registration successful
                            val user = auth.currentUser

                            // Save user data to Firestore
                            user?.let {
                                val userData = HashMap<String, Any>()
                                userData["email"] = email
                                userData["password"] = password

                                db.collection("users")
                                    .document(user.uid)
                                    .set(userData)
                                    .addOnSuccessListener {
                                        // User data saved to Firestore
                                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

                                    }
                                    .addOnFailureListener { e ->
                                        // Handle Firestore write error
                                        Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                                    }
                            }

                            // Redirect to the next activity or perform any other action
                            startActivity(Intent(this, Login::class.java))
                        } else {
                            // Registration failed, handle the error
                            val exception = task.exception
                            // Display an error message or log the exception
                            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        // Set on click listener for text view login
        tvLogin.setOnClickListener {
            // Navigate to the login screen
            startActivity(Intent(this, Login::class.java))
        }


    }
}