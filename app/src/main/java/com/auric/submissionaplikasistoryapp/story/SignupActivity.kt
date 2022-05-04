package com.auric.submissionaplikasistoryapp.story

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ObjectAnimator.ofFloat
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.auric.submissionaplikasistoryapp.R
import com.auric.submissionaplikasistoryapp.databinding.ActivitySignupBinding
import com.auric.submissionaplikasistoryapp.model.RegisterUser
import com.auric.submissionaplikasistoryapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupView()
        playAnimation()
        checkUserStatus()
        setupAction()

        userViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        userViewModel.userStatus.observe(this) {
            if(!it) {
                Toast.makeText(this,R.string.serverfail, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkUserStatus() {
        userViewModel.getUserPreferences().observe(this) {
            if(it.token.trim() != "") {
                val intent = Intent(this@SignupActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.apply {
            backtosign.setOnClickListener {
                startActivity(Intent(this@SignupActivity, SigninActivity::class.java))
            }

            signupbutton.setOnClickListener{
                register()
            }
        }
    }


    private fun register() {
        val name = binding.nameEditTextLayout.text.toString().trim()
        val email = binding.emailEditTextLayout.text.toString().trim()
        val password = binding.passwordEditTextLayout.text.toString().trim()
        when {
            name.isEmpty() -> {
                binding.nameEditTextLayout.error = getString(R.string.fill_name)
            }

            email.isEmpty() -> {
                binding.emailEditTextLayout.error = getString(R.string.fill_email)
            }
            password.isEmpty() -> {
                binding.passwordEditTextLayout.error = getString(R.string.fill_password)
            }
            else -> {
                if (checkEmail(email) && password.length >= 6) {
                    userViewModel.userRegister(RegisterUser(name, email, password))
                    Toast.makeText(this, R.string.accountok, Toast.LENGTH_SHORT)
                        .show()
                    val mainIntent =
                        Intent(this@SignupActivity, SigninActivity::class.java)
                    startActivity(mainIntent)
                } else {
                    Toast.makeText(this, R.string.datamust, Toast.LENGTH_SHORT)
                        .show()
                }

                userViewModel.loginResult.observe(this) {
                    if (it.token != "") {
                        userViewModel.saveUserPreference(it)
                    }

                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }

        binding.apply {
            signupbutton.isEnabled = !isLoading
            backtosign.isEnabled = !isLoading
            nameEditTextLayout.isEnabled = !isLoading
            emailEditTextLayout.isEnabled = !isLoading
            passwordEditTextLayout.isEnabled =!isLoading
        }
    }



        private fun playAnimation() {
            ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
                duration = 6000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()
            val title = ofFloat(binding.titlesignup, View.ALPHA, 1f).setDuration(250)
            val nameEditTextLayout =
                ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(250)
            val emailEditTextLayout =
                ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(250)
            val passwordEditTextLayout =
                ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(250)
            val signup = ofFloat(binding.signupbutton, View.ALPHA, 1f).setDuration(250)
            val login = ofFloat(binding.backtosign, View.ALPHA, 1f).setDuration(250)

            AnimatorSet().apply {
                playSequentially(
                    title,
                    nameEditTextLayout,
                    emailEditTextLayout,
                    passwordEditTextLayout,
                    signup,
                    login
                )
                startDelay = 500
            }.start()
        }


        private fun checkEmail(email : String) : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
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
}