package com.dicoding.picodiploma.loginwithanimation.view.login

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.story.StoryActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding
    private var isProcessing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        startAnimations()
        observeLoginResponse()
    }

    private fun setupView() {
        supportActionBar?.hide()
        binding.loadingCard.visibility = View.GONE
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (!isProcessing) {
                isProcessing = true
                showLoading(true)
                viewModel.login(email, password)
            }
        }
    }

    private fun observeLoginResponse() {
        lifecycleScope.launch {
            viewModel.loginResponse.collect { response ->
                isProcessing = false
                showLoading(false)
                if (response != null) {
                    if (response.error) {
                        showErrorDialog(response.message)
                    } else {
                        val token = response.loginResult.token
                        viewModel.saveSession(UserModel(binding.edLoginEmail.text.toString(), token))
                        showSuccessDialog()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingCard.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Login Gagal")
            setMessage(message)
            setPositiveButton("OK") { _, _ -> }
            create()
            show()
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Login Berhasil")
            setMessage("Selamat datang!")
            setPositiveButton("Lanjut") { _, _ ->
                val intent = Intent(this@LoginActivity, StoryActivity::class.java)
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun startAnimations() {
        val translationX = ObjectAnimator.ofFloat(binding.imageView, "translationX", -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        translationX.start()

        binding.titleTextView.alpha = 0f
        binding.titleTextView.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(0)
            .start()

        binding.messageTextView.alpha = 0f
        binding.messageTextView.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(200)
            .start()

        binding.emailTextView.alpha = 0f
        binding.emailTextView.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(500)
            .start()

        binding.emailEditTextLayout.alpha = 0f
        binding.emailEditTextLayout.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(500)
            .start()

        binding.passwordTextView.alpha = 0f
        binding.passwordTextView.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(800)
            .start()

        binding.passwordEditTextLayout.alpha = 0f
        binding.passwordEditTextLayout.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(800)
            .start()

        binding.loginButton.alpha = 0f
        binding.loginButton.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(1100)
            .start()
    }

    override fun onStart() {
        super.onStart()

        lifecycleScope.launch {
            viewModel.loginResponse.collect { response ->
                if (isProcessing) {
                    isProcessing = false
                }

                if (response != null) {
                    if (response.error) {
                        showErrorDialog(response.message)
                    } else {
                        val token = response.loginResult.token
                        viewModel.saveSession(UserModel(binding.edLoginEmail.text.toString(), token))
                        showSuccessDialog()
                    }
                }
            }
        }
    }
}
