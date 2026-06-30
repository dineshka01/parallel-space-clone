package com.yourpackage.parallelspace

import android.app.Application
import android.content.Context
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.client.ClientConfiguration

class ParallelApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        try {
            // BlackBox Engine Initialize
            BlackBoxCore.get().doAttachBaseContext(base, object : ClientConfiguration() {
                override fun getHostPackageName(): String {
                    return base?.packageName ?: "com.yourpackage.parallelspace"
                }

                override fun getBinderProviderAuthority(): String {
                    return "${base?.packageName}.virtual_provider"
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate() {
        super.onCreate()
        try {
            BlackBoxCore.get().doCreate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
