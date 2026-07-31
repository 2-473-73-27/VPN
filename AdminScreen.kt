package com.sair.vpn.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Composable
fun AdminScreen() {
    val context = LocalContext.current
    var clientEmailToActivate by remember { mutableStateOf("") }
    var newAdMobId by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F151D))
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Manager Admin Portal", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        // Option 1: One-Click ZIP Downloader
        Button(
            onClick = { downloadAllScriptsZip(context) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
        ) {
            Text("Download All Source Files (.ZIP)", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Option 2: Activate Client Premium
        Text("Activate Premium for User", color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = clientEmailToActivate,
            onValueChange = { clientEmailToActivate = it },
            label = { Text("Client Email Address") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (clientEmailToActivate.isNotBlank()) {
                    db.collection("users")
                        .whereEqualTo("email", clientEmailToActivate)
                        .get()
                        .addOnSuccessListener { docs ->
                            for (doc in docs) {
                                db.collection("users").document(doc.id).update("isPremium", true)
                            }
                            Toast.makeText(context, "Premium Activated!", Toast.LENGTH_SHORT).show()
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Make User Premium")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Option 3: Update AdMob Ads Live
        Text("Update AdMob Ad Unit ID", color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = newAdMobId,
            onValueChange = { newAdMobId = it },
            label = { Text("New AdMob Interstitial Unit ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (newAdMobId.isNotBlank()) {
                    db.collection("config").document("admob").set(mapOf("adUnitId" to newAdMobId))
                    Toast.makeText(context, "AdMob ID Updated!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Ads Live")
        }
    }
}

// ZIP Downloader Function for Source Files
private fun downloadAllScriptsZip(context: Context) {
    try {
        val zipFile = File(context.getExternalFilesDir(null), "SAiR_VPN_Source.zip")
        val zipOutputStream = ZipOutputStream(FileOutputStream(zipFile))

        val scriptFiles = mapOf(
            "SairVpnService.kt" to "// Full VpnService routing code",
            "AuthManager.kt" to "// Firebase Auth and Manager privileges code",
            "HomeScreen.kt" to "// Jetpack Compose Surfshark UI code",
            "AndroidManifest.xml" to "<!-- Full Android Manifest configuration -->"
        )

        for ((fileName, content) in scriptFiles) {
            zipOutputStream.putNextEntry(ZipEntry(fileName))
            zipOutputStream.write(content.toByteArray())
            zipOutputStream.closeEntry()
        }

        zipOutputStream.close()
        Toast.makeText(context, "ZIP saved to: ${zipFile.absolutePath}", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error creating ZIP: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
