package com.auric.submissionaplikasistoryapp.story.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.auric.submissionaplikasistoryapp.databinding.ItemRowStoryBinding
import com.auric.submissionaplikasistoryapp.model.ListStoryItem
import com.auric.submissionaplikasistoryapp.story.DetailStoryActivity
import com.bumptech.glide.Glide

class StoryAdapter: PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val dataItem = getItem(position)
        if (dataItem != null) {
            holder.bind(dataItem)
        }
    }

    class StoryViewHolder(private val binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listStoryItem: ListStoryItem) {
            binding.apply {
                Glide.with(itemView)
                    .load(listStoryItem.photoUrl)
                    .into(binding.photo)
                binding.name.text = listStoryItem.name
                binding.desc.text = listStoryItem.description
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.photo, "profile"),
                        Pair(binding.name, "name"),
                        Pair(binding.desc, "description"),
                    )
                intent.putExtra("ListStoryItem", listStoryItem)
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {

            override fun areItemsTheSame(oldStory: ListStoryItem, newStory: ListStoryItem): Boolean {
                return oldStory == newStory
            }

            override fun areContentsTheSame(oldStory: ListStoryItem, newStory: ListStoryItem): Boolean {
                return oldStory.name == newStory.name &&
                        oldStory.photoUrl == newStory.photoUrl &&
                        oldStory.id == newStory.id &&
                        oldStory.createdAt == newStory.createdAt &&
                        oldStory.description == newStory.description
            }
        }
    }
}