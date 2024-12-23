package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val signUpViewModel: SignUpViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private var isProcessing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        observeSignUpResponse()
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            if (name.isNotEmpty() && !isProcessing) {
                toggleLoading(true)
                isProcessing = true
                signUpViewModel.signUp(name, email, password)
            } else {
                binding.nameEditTextLayout.error = "Nama harus diisi"
            }
        }
    }

    private fun observeSignUpResponse() {
        lifecycleScope.launch {
            signUpViewModel.signUpResponse.collect { response ->
                isProcessing = false
                toggleLoading(false)
                response?.let {
                    if (it.error == false) {
                        showSuccessDialog()
                    } else {
                        showErrorDialog(it.message ?: "Sign Up Failed")
                    }
                }
            }
        }
    }

    private fun toggleLoading(show: Boolean) {
        binding.loadingCard.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Oops!")
            setMessage(message)
            setPositiveButton("Retry") { _, _ -> }
            create()
            show()
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Yeah!")
            setMessage("Akun dengan ${binding.edRegisterEmail.text} sudah jadi nih. Yuk, login dan mulai buat story!")
            setPositiveButton("Lanjut") { _, _ -> finish() }
            create()
            show()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            startAnimations()
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

        binding.nameTextView.alpha = 0f
        binding.nameTextView.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(300)
            .start()

        binding.nameEditTextLayout.alpha = 0f
        binding.nameEditTextLayout.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(300)
            .start()

        binding.emailTextView.alpha = 0f
        binding.emailTextView.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(600)
            .start()

        binding.emailEditTextLayout.alpha = 0f
        binding.emailEditTextLayout.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(600)
            .start()

        binding.passwordTextView.alpha = 0f
        binding.passwordTextView.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(900)
            .start()

        binding.passwordEditTextLayout.alpha = 0f
        binding.passwordEditTextLayout.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(900)
            .start()

        binding.signupButton.alpha = 0f
        binding.signupButton.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(1200)
            .start()
    }
}
