package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.view.story.StoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userPreference = UserPreference(applicationContext.dataStore)

        lifecycleScope.launch {
            val userSession = userPreference.getSession().first()
            if (userSession.isLogin && userSession.token.isNotEmpty()) {
                navigateToStory()
            } else {
                initializeMainView()
            }
        }
    }

    private fun navigateToStory() {
        val intent = Intent(this, StoryActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun initializeMainView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        startAnimations()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.continueButton.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startAnimations() {
        val imageViewAnimator = ObjectAnimator.ofFloat(binding.imageView, "translationX", -30f, 30f)
        imageViewAnimator.duration = 6000
        imageViewAnimator.repeatCount = ObjectAnimator.INFINITE
        imageViewAnimator.repeatMode = ObjectAnimator.REVERSE
        imageViewAnimator.start()

        binding.messageTextView.alpha = 0f
        binding.messageTextView.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(0)
            .start()

        binding.instructionTextView.alpha = 0f
        binding.instructionTextView.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(500)
            .start()

        binding.continueButton.alpha = 0f
        binding.continueButton.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(1000)
            .start()
    }
}
