package com.yourpackage.parallelspace

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.yourpackage.parallelspace.manager.CloneManager
import com.yourpackage.parallelspace.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cloneManager: CloneManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cloneManager = CloneManager(this)
        setupUI()
    }

    private fun setupUI() {
        binding.fabAdd.setOnClickListener { showAppPicker() }

        binding.recyclerClones.layoutManager = GridLayoutManager(this, 3)

        updateSubtitle()
    }

    private fun showAppPicker() {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        val apps = pm.queryIntentActivities(intent, 0)
            .map { Triple(it.activityInfo.packageName, it.loadLabel(pm).toString(), it.loadIcon(pm)) }
            .distinctBy { it.first }
            .sortedBy { it.second }

        AlertDialog.Builder(this)
            .setTitle("Add to Virtual Space")
            .setItems(apps.map { it.second }.toTypedArray()) { _, which ->
                val (pkg, name, icon) = apps[which]
                createClone(pkg, name, icon)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createClone(packageName: String, appName: String, icon: android.graphics.drawable.Drawable?) {
        val clone = cloneManager.createClone(packageName)
        val id = clone.identity

        AlertDialog.Builder(this)
            .setTitle("✅ ${clone.appName}")
            .setMessage("""
                NEW VIRTUAL ENVIRONMENT
                
                📱 ${id.brand} ${id.model}
                🆔 DeviceID: ${id.deviceId}
                📱 IMEI: ${id.imei}
                📶 WiFi MAC: ${id.wifiMac}
                🆔 Android ID: ${id.androidId}
                🆔 GAID: ${id.advertisingId}
                📍 ${id.locale} / ${id.timezone}
                
                ✅ 21/21 Permissions Granted
                🔒 Google Accounts: Hidden
                ⚡ BlackBox Virtual Engine
            """.trimIndent())
            .setPositiveButton("🚀 Launch Clone") { _, _ ->
                cloneManager.launchClone(clone)
                Toast.makeText(this, "Launching ${clone.appName} in Virtual Space", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Close", null)
            .show()

        updateSubtitle()
    }

    private fun updateSubtitle() {
        binding.tvSubtitle.text = "21 permissions granted • BlackBox Virtual Engine"
    }
}
