package com.practical.photocutpro.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practical.photocutpro.MainActivity
import com.practical.photocutpro.R
import com.practical.photocutpro.databinding.ActivityLaunchBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class LaunchActivity : AppCompatActivity() {

    lateinit var binding : ActivityLaunchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.animationView.playAnimation()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@LaunchActivity , MainActivity::class.java))
            binding.animationView.pauseAnimation()
            finish()
        }, 5000)

    }



}