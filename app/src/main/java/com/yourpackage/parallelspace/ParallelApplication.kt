package com.yourpackage.parallelspace

import android.app.Application
import android.content.Context
import android.util.Log
import top.niunaijun.blackbox.BlackBoxCore

class ParallelApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        try {
            super.attachBaseContext(base)
            try {
                BlackBoxCore.get().closeCodeInit()
            } catch (e: Exception) {
                Log.e("ParallelApp", "closeCodeInit error: ${e.message}")
            }
            try {
                BlackBoxCore.get().onBeforeMainApplicationAttach(this, base)
            } catch (e: Exception) {
                Log.e("ParallelApp", "onBeforeMainApplicationAttach error: ${e.message}")
            }
            try {
                BlackBoxCore.get().onAfterMainApplicationAttach(this, base)
            } catch (e: Exception) {
                Log.e("ParallelApp", "onAfterMainApplicationAttach error: ${e.message}")
            }
        } catch (e: Exception) {
            Log.e("ParallelApp", "Critical error in attachBaseContext: ${e.message}")
        }
    }

    override fun onCreate() {
        super.onCreate()
    }
}
