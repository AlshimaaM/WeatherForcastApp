package com.example.myapplication.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.SplashActivityBinding
import com.example.myapplication.viewmodel.SplashState
import com.example.myapplication.viewmodel.SplashViewModel


class SplashActivity : AppCompatActivity() {


    lateinit var mBinding: SplashActivityBinding

    val viewModel by lazy {
        ViewModelProvider(this)[SplashViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = SplashActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
       ///setUpStatusBar(this, 1)

        viewModel.liveData.observe(this, Observer {
            when (it) {
                is SplashState.MainActivity -> {
                    goToMainActivity()
                }
            }
        })



        val a: Animation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        a.reset()

       mBinding.imgSplashLogo.clearAnimation()
        mBinding.imgSplashLogo.startAnimation(a)

    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        finish()
    }

}