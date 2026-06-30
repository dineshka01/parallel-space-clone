package com.yourpackage.parallelspace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yourpackage.parallelspace.databinding.ActivityCloneDetailBinding

class CloneDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCloneDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCloneDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cloneId = intent.getStringExtra("clone_id") ?: return
        val appName = intent.getStringExtra("app_name") ?: "Unknown"

        binding.tvAppName.text = appName
        binding.tvCloneId.text = "Clone ID: $cloneId"

        binding.btnLaunch.setOnClickListener {
            finish()
        }
    }
}
