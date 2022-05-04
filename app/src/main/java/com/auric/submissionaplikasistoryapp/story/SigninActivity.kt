package com.auric.submissionaplikasistoryapp.story

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.auric.submissionaplikasistoryapp.R

import com.auric.submissionaplikasistoryapp.databinding.ActivitySigninBinding
import com.auric.submissionaplikasistoryapp.model.LoginUser
import com.auric.submissionaplikasistoryapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SigninActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        playAnimation()
        login()
        setupAction()
        userViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        userViewModel.userStatus.observe(this) {
            if (!it) {
                Toast.makeText(this, R.string.invalid, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun login() {
        userViewModel.getUserPreferences().observe(this) {
            if (it.token.trim() != "") {
                val intent = Intent(this@SigninActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.apply {
            signinbutton.setOnClickListener {
                userLogin()
            }
            signupbutton.setOnClickListener {
                startActivity(Intent(this@SigninActivity, SignupActivity::class.java))
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loginProgressBar.visibility = View.VISIBLE
        } else {
            binding.loginProgressBar.visibility = View.GONE
        }

        binding.apply {
            signinbutton.isEnabled = !isLoading
            signupbutton.isEnabled = !isLoading
            emailEditTextLayout.isEnabled = !isLoading
            passwordEditTextLayout.isEnabled = !isLoading
        }
    }

    private fun checkEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun userLogin() {
        val email = binding.emailEditTextLayout.text.toString()
        val password = binding.passwordEditTextLayout.text.toString()

        when {
            email.isEmpty() -> {
                binding.emailEditTextLayout.error = getString(R.string.fill_email)
            }
            password.isEmpty() -> {
                binding.passwordEditTextLayout.error = getString(R.string.fill_password)
            }
            else -> {
                if(checkEmail(email) &&  password.length >= 6) {
                    userViewModel.userLogin(LoginUser(email, password))
                } else {
                    Toast.makeText(this, R.string.datamust, Toast.LENGTH_SHORT).show()
                }

                userViewModel.loginResult.observe(this) {
                    userViewModel.saveUserPreference(it)
                }
            }
        }

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

        private fun playAnimation() {
            ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
                duration = 6000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()

            val title = ObjectAnimator.ofFloat(binding.titlesignin, View.ALPHA, 1f).setDuration(250)
            val message =
                ObjectAnimator.ofFloat(binding.messagesignin, View.ALPHA, 1f).setDuration(250)
            val emailEditTextLayout =
                ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(250)
            val passwordEditTextLayout =
                ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f)
                    .setDuration(250)
            val login =
                ObjectAnimator.ofFloat(binding.signinbutton, View.ALPHA, 1f).setDuration(250)
            val signup =
                ObjectAnimator.ofFloat(binding.signupbutton, View.ALPHA, 1f).setDuration(250)

            AnimatorSet().apply {
                playSequentially(title,
                    message,
                    emailEditTextLayout,
                    passwordEditTextLayout,
                    login,
                    signup)
                startDelay = 500
            }.start()
        }
}
