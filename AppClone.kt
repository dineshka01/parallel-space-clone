package com.yourpackage.parallelspace.model

import android.graphics.drawable.Drawable

data class AppClone(
    val id: String,
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val identity: DeviceIdentity,
    val grantedPermissions: List<String>,
    val pendingPermissions: List<String>,
    val isGoogleService: Boolean,
    val userId: Int = -1,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsed: Long = 0
)
