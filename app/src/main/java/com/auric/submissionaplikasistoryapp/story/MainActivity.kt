package com.auric.submissionaplikasistoryapp.story

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.auric.submissionaplikasistoryapp.R
import com.auric.submissionaplikasistoryapp.databinding.ActivityMainBinding
import com.auric.submissionaplikasistoryapp.story.adapter.PagingAdapter
import com.auric.submissionaplikasistoryapp.story.adapter.StoryAdapter
import com.auric.submissionaplikasistoryapp.viewmodel.StoryViewModel
import com.auric.submissionaplikasistoryapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val userViewModel by viewModels<UserViewModel>()
    private val storyViewModel by viewModels<StoryViewModel>()
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Story App"

        setupAdapter()
        setupAction()
        checkUserStatus()

        storyViewModel.isLoading.observe(this) {
            showLoading(it)
        }

    }

    private fun checkUserStatus() {
        userViewModel.getUserPreferences().observe(this) {
            if (it.token.trim() == "") {
                val intent = Intent(this@MainActivity, SigninActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                setupData()
              userViewModel.getUserPreferences().observe(this){
                  binding.nameTextView.text = getString(R.string.greeting, it.name)
                }
            }
        }
    }

    private fun setupAction() {
        binding.fabadd.setOnClickListener {
            val intent = Intent(this@MainActivity, AddstoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupAdapter() {
        storyAdapter = StoryAdapter()
        binding.rvstory.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.rvstory.adapter = storyAdapter.withLoadStateFooter(
            footer = PagingAdapter {
                storyAdapter.retry()
            }
        )
    }

    private fun setupData() {
        storyViewModel.allStory.observe(this) { pagingData ->
            storyAdapter.submitData(lifecycle, pagingData)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                logoutUser()
            }
            R.id.language -> {
                startActivity(Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.menu_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
        }
        return true
    }

    private fun logoutUser() {
        userViewModel.clearUserPreference()
        checkUserStatus()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.storiesProgressBar.visibility = View.VISIBLE
        } else {
            binding.storiesProgressBar.visibility = View.GONE
        }
    }
}
