package com.yourpackage.parallelspace.manager

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import top.niunaijun.blackbox.BlackBoxCore
import com.yourpackage.parallelspace.identity.IdentityGenerator
import com.yourpackage.parallelspace.model.AppClone
import com.yourpackage.parallelspace.model.DeviceIdentity
import org.json.JSONArray
import org.json.JSONObject

class CloneManager(private val context: Context) {

    private val TAG = "CloneManager"
    private val identityGenerator = IdentityGenerator()
    private val prefs = context.getSharedPreferences("clones_db", Context.MODE_PRIVATE)

    companion object {
        val GRANTED_PERMISSIONS = listOf(
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.INTERNET",
            "android.permission.VIBRATE",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.WAKE_LOCK",
            "android.permission.USE_BIOMETRIC",
            "android.permission.ACCESS_WIFI_STATE",
            "android.permission.FOREGROUND_SERVICE",
            "android.permission.ACCESS_ADSERVICES_ATTRIBUTION",
            "com.google.android.gms.permission.AD_ID",
            "com.google.android.c2dm.permission.RECEIVE",
            "com.android.vending.BILLING",
            "android.permission.USE_FINGERPRINT",
            "com.android.vending.CHECK_LICENSE",
            "android.permission.ACCESS_ADSERVICES_AD_ID",
            "android.permission.ACCESS_ADSERVICES_TOPICS",
            "hk.elftech.block.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION",
            "com.google.android.finsky.permission.BIND_GET_INSTALL_REFERRER_SERVICE",
            "hk.elftech.block.permission.RONG_ACCESS_RECEIVER",
            "hk.elftech.block.permission.RONG_BRIDGE_ACTIVITY"
        )
        // Default userId 0 = first virtual user slot
        const val DEFAULT_USER_ID = 0
    }

    fun createClone(packageName: String): AppClone {
        val pm = context.packageManager
        val appInfo = try { pm.getApplicationInfo(packageName, 0) } catch (e: Exception) { null }
        val appName = if (appInfo != null) {
            pm.getApplicationLabel(appInfo).toString()
        } else {
            packageName
        }
        val icon: Drawable? = try {
            appInfo?.let { pm.getApplicationIcon(it) }
        } catch (e: Exception) { null }

        val identity = identityGenerator.generate()

        // Install into BlackBox virtual environment (userId 0 = default virtual space)
        val apkPath = appInfo?.sourceDir
        if (apkPath != null) {
            try {
                BlackBoxCore.get().installPackageAsUser(apkPath, DEFAULT_USER_ID)
                Log.d(TAG, "Installed $packageName into virtual space")
            } catch (e: Exception) {
                Log.e(TAG, "Install error for $packageName: ${e.message}")
            }
        }

        val cloneNum = getCloneCount(packageName) + 1
        val clone = AppClone(
            id = "${packageName}_${System.currentTimeMillis()}",
            packageName = packageName,
            appName = "$appName #$cloneNum",
            icon = icon,
            identity = identity,
            grantedPermissions = GRANTED_PERMISSIONS,
            pendingPermissions = emptyList(),
            isGoogleService = packageName.startsWith("com.google.") || packageName.startsWith("com.android."),
            userId = DEFAULT_USER_ID,
            createdAt = System.currentTimeMillis()
        )
        saveClone(clone)
        return clone
    }

    fun launchClone(clone: AppClone) {
        try {
            BlackBoxCore.get().launchApk(clone.packageName, clone.userId)
        } catch (e: Exception) {
            Log.e(TAG, "Launch error: ${e.message}")
        }
    }

    fun getAllClones(): List<AppClone> {
        val json = prefs.getString("clones", "[]") ?: "[]"
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                AppClone(
                    id = obj.getString("id"),
                    packageName = obj.getString("packageName"),
                    appName = obj.getString("appName"),
                    icon = null,
                    identity = identityGenerator.generate(),
                    grantedPermissions = GRANTED_PERMISSIONS,
                    pendingPermissions = emptyList(),
                    isGoogleService = obj.optBoolean("isGoogleService", false),
                    userId = obj.optInt("userId", DEFAULT_USER_ID),
                    createdAt = obj.optLong("createdAt", 0)
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getClonesForApp(packageName: String) = getAllClones().filter { it.packageName == packageName }
    private fun getCloneCount(pkg: String) = getClonesForApp(pkg).size

    fun deleteClone(cloneId: String) {
        val json = prefs.getString("clones", "[]") ?: "[]"
        try {
            val arr = JSONArray(json)
            val newArr = JSONArray()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                if (obj.getString("id") != cloneId) newArr.put(obj)
            }
            prefs.edit().putString("clones", newArr.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Delete error: ${e.message}")
        }
    }

    private fun saveClone(clone: AppClone) {
        val json = prefs.getString("clones", "[]") ?: "[]"
        try {
            val arr = JSONArray(json)
            arr.put(JSONObject().apply {
                put("id", clone.id)
                put("packageName", clone.packageName)
                put("appName", clone.appName)
                put("userId", clone.userId)
                put("isGoogleService", clone.isGoogleService)
                put("createdAt", clone.createdAt)
            })
            prefs.edit().putString("clones", arr.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Save error: ${e.message}")
        }
    }
}
