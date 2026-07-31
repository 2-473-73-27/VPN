package com.sair.vpn.service

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.InetSocketAddress
import java.nio.channels.DatagramChannel
import kotlin.concurrent.thread

class SairVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    @Volatile private var isRunning = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == "DISCONNECT") {
            stopVpn()
            return START_NOT_STICKY
        }

        val serverIp = intent?.getStringExtra("SERVER_IP") ?: "185.220.101.5"
        startVpnTunnel(serverIp)

        return START_STICKY
    }

    private fun startVpnTunnel(serverIp: String) {
        try {
            val builder = Builder()
                .setSession("SA!R VPN Session")
                .addAddress("10.8.0.2", 24)
                .addDnsServer("1.1.1.1")
                .addDnsServer("8.8.8.8")
                .addRoute("0.0.0.0", 0)

            // Protect raw connection socket from routing through itself
            val tunnel = DatagramChannel.open()
            protect(tunnel.socket())
            tunnel.connect(InetSocketAddress(serverIp, 1194))

            vpnInterface = builder.establish()
            isRunning = true

            // Real-time packet loop to forward tun traffic
            thread {
                val input = FileInputStream(vpnInterface?.fileDescriptor)
                val output = FileOutputStream(vpnInterface?.fileDescriptor)
                val buffer = ByteArray(32767)

                while (isRunning) {
                    val length = input.read(buffer)
                    if (length > 0) {
                        // Forward packets through protected channel
                        tunnel.write(java.nio.ByteBuffer.wrap(buffer, 0, length))
                    }
                }
            }

            Log.d("SAIR_VPN", "Tunnel connected and routing traffic through $serverIp")
        } catch (e: Exception) {
            Log.e("SAIR_VPN", "Tunnel connection error", e)
            stopVpn()
        }
    }

    private fun stopVpn() {
        isRunning = false
        try {
            vpnInterface?.close()
            vpnInterface = null
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        } catch (e: Exception) {
            Log.e("SAIR_VPN", "Error closing tunnel", e)
        }
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }
}
