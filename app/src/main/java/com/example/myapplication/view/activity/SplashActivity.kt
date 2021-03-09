package com.example.myapplication.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.SplashActivityBinding
import com.example.myapplication.provider.Setting
import com.example.myapplication.util.ContextUtils.Companion.setLocale
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {

    lateinit var binding: SplashActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
       /*  setLocale(this,"en")
        Log.d("lan","language"+"ar")
        Log.d("lan","language"+Setting.unitSystem)*/
        GlobalScope.launch {
            val animation: Animation = AnimationUtils.loadAnimation(this@SplashActivity, R.anim.bottom_to_top)
            binding.imgSplashLogo.setVisibility(View.VISIBLE)
            animation.reset()

            binding.imgSplashLogo.startAnimation(animation)
            delay(3000)
            var intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

}