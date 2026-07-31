package com.sair.vpn.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sair.vpn.model.VpnServer

@Composable
fun HomeScreen(
    selectedServer: VpnServer,
    onSelectServerClick: () -> Unit
) {
    val context = LocalContext.current
    var isConnected by remember { mutableStateOf(false) }

    val telegramUrl = "https://t.me/+593988170503"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F151D))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("SA!R VPN", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            
            // First Page Telegram Button
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(telegramUrl))
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0088CC)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Contact Telegram", color = Color.White, fontSize = 12.sp)
            }
        }

        // Server Selection Card
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onSelectServerClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1D2633))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Selected Server", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        selectedServer.countryName.ifEmpty { "Select Server" },
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text("CHANGE >", color = Color(0xFF1DA1F2), fontWeight = FontWeight.Bold)
            }
        }

        // Central Connect Toggle Button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(180.dp)
                .background(
                    if (isConnected) Color(0xFF00E676).copy(alpha = 0.2f) else Color(0xFF1DA1F2).copy(alpha = 0.2f),
                    shape = CircleShape
                )
                .clickable { isConnected = !isConnected }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(130.dp)
                    .background(
                        if (isConnected) Color(0xFF00E676) else Color(0xFF1DA1F2),
                        shape = CircleShape
                    )
            ) {
                Text(
                    if (isConnected) "STOP" else "CONNECT",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        // Real-Time IP Verification Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1D2633))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Virtual IP", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        if (isConnected) selectedServer.serverIp else "---.---.---.---",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Status", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        if (isConnected) "PROTECTED" else "UNPROTECTED",
                        color = if (isConnected) Color(0xFF00E676) else Color(0xFFFF5252),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
