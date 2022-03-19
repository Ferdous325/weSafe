package com.bauet.wesafe.ui.splash_screen

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bauet.wesafe.databinding.ActivityMainBinding
import com.bauet.wesafe.ui.home.HomeActivity
import com.bauet.wesafe.ui.login.LoginActivity
import com.bauet.wesafe.utils.SessionManager

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        animateView()
    }

    private fun animateView() {
        val animTranslation = ObjectAnimator.ofFloat(binding, "translationX", 0f, 450f)
        animTranslation.duration = 2000

        val animSet = AnimatorSet()
        animSet.play(animTranslation)//.with(animLogoFadeIn)
        animSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {
                goToHome()
            }
        })
        animSet.start()
    }

    private fun goToHome() {
        if (SessionManager.isLogin) {
            Intent(this@SplashActivity, HomeActivity::class.java).apply {
                if (intent.extras != null) {
                    putExtras(intent.extras!!)
                }
            }.also {
                startActivity(it)
            }
        } else {
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        }
        finish()
    }
}