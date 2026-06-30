package com.yourpackage.parallelspace

import android.app.Application
import top.niunaijun.blackbox.BlackBoxCore

class ParallelApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        BlackBoxCore.get().doAttachBaseContext(this)
    }
}
