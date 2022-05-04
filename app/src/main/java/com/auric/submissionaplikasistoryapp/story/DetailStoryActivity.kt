package com.auric.submissionaplikasistoryapp.story

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.auric.submissionaplikasistoryapp.databinding.ActivityDetailStoryBinding
import com.auric.submissionaplikasistoryapp.model.ListStoryItem
import com.bumptech.glide.Glide

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail"
        bind()
    }

    private fun bind() {
        val story = intent.getParcelableExtra<ListStoryItem>("ListStoryItem") as ListStoryItem
        binding.apply {
            Glide.with(applicationContext)
                .load(story.photoUrl)
                .into(binding.profileImageView)
            binding.nameTextView.text = story.name
            binding.descTextView.text = story.description
        }
    }
}