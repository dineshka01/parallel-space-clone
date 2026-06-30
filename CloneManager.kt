package com.yourpackage.parallelspace.manager

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.core.system.user.BUserInfo
import com.yourpackage.parallelspace.identity.IdentityGenerator
import com.yourpackage.parallelspace.model.AppClone
import com.yourpackage.parallelspace.model.DeviceIdentity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class CloneManager(private val context: Context) {

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
    }

    fun createClone(packageName: String): AppClone {
        val pm = context.packageManager
        val appInfo = try { pm.getApplicationInfo(packageName, 0) } catch (e: Exception) { null }
        val appName = pm.getApplicationLabel(appInfo)?.toString() ?: packageName
        val icon = appInfo?.let { pm.getApplicationIcon(it) }

        // Step 1: Generate fresh random identity
        val identity = identityGenerator.generate()

        // Step 2: Create new virtual user in BlackBox
        val userInfo = BUserInfo().apply {
            name = "Clone_${identity.deviceId.take(6)}"
        }
        val userId = BlackBoxCore.get().createUser(userInfo)

        // Step 3: Install app into BlackBox
        val apkPath = appInfo?.sourceDir
        if (apkPath != null) {
            BlackBoxCore.get().installPackageAsUser(File(apkPath), userId)
        }

        val cloneNum = getCloneCount(packageName) + 1

        return AppClone(
            id = userId.toString(),
            packageName = packageName,
            appName = "$appName #$cloneNum",
            icon = icon,
            identity = identity,
            grantedPermissions = GRANTED_PERMISSIONS,
            pendingPermissions = emptyList(),
            isGoogleService = packageName.startsWith("com.google.") || packageName.startsWith("com.android."),
            userId = userId,
            createdAt = System.currentTimeMillis()
        ).also { saveClone(it) }
    }

    fun launchClone(clone: AppClone) {
        BlackBoxCore.get().launchApk(clone.packageName, clone.userId)
    }

    fun getAllClones(): List<AppClone> {
        val json = prefs.getString("clones", "[]") ?: "[]"
        // Parse from JSON — simplified
        return emptyList()
    }

    fun getClonesForApp(packageName: String): List<AppClone> = getAllClones().filter { it.packageName == packageName }
    private fun getCloneCount(pkg: String): Int = getClonesForApp(pkg).size

    fun deleteClone(cloneId: String) {
        try { BlackBoxCore.get().deleteUser(cloneId.toInt()) } catch (_: Exception) {}
    }

    private fun saveClone(clone: AppClone) {
        val json = prefs.getString("clones", "[]") ?: "[]"
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
    }
}
