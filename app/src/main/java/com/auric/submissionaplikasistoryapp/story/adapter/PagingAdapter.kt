package com.auric.submissionaplikasistoryapp.story.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.auric.submissionaplikasistoryapp.databinding.ItemRowLoadingBinding

class PagingAdapter(private val retry: () -> Unit) : LoadStateAdapter<PagingAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemRowLoadingBinding, retry: () -> Unit) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnRetry.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if(loadState is LoadState.Error) {
                binding.tvError.text = loadState.error.localizedMessage
            }
            binding.apply {
                loadingBar.isVisible = loadState is LoadState.Loading
                btnRetry.isVisible = loadState is LoadState.Loading
                tvError.isVisible = loadState is LoadState.Loading
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        val binding = ItemRowLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, retry)
    }
}