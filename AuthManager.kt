package com.sair.vpn.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    const val MANAGER_EMAIL = "sairahmadani233@gmail.com"

    fun registerOrLoginClient(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            onResult(false, "Email and Password cannot be empty.")
            return
        }

        // Try logging in first
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                onResult(true, "Signed in successfully!")
            }
            .addOnFailureListener {
                // If account does not exist, create a new one automatically
                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener { result ->
                        val uid = result.user?.uid ?: ""
                        val isManager = email.equals(MANAGER_EMAIL, ignoreCase = true)
                        
                        val userMap = hashMapOf(
                            "email" to email,
                            "role" to if (isManager) "manager" else "client",
                            "isPremium" to isManager
                        )

                        db.collection("users").document(uid).set(userMap)
                        onResult(true, "Account created successfully!")
                    }
                    .addOnFailureListener { err ->
                        onResult(false, err.localizedMessage ?: "Authentication failed.")
                    }
            }
    }

    fun isUserPremium(onCheck: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onCheck(false)
            return
        }

        if (currentUser.email.equals(MANAGER_EMAIL, ignoreCase = true)) {
            onCheck(true)
            return
        }

        db.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { doc ->
                val isPrem = doc.getBoolean("isPremium") ?: false
                onCheck(isPrem)
            }
            .addOnFailureListener { onCheck(false) }
    }
}
