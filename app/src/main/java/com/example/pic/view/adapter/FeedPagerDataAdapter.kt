package com.example.pic.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.pic.databinding.FeedItemBinding
import com.example.pic.model.res.Feed


class FeedPagerDataAdapter(private val onClick: (Feed?, Boolean) -> Unit) :
    PagingDataAdapter<Feed, FeedPagerDataAdapter.FeedViewHolder>(FeedDiffUtil()) {

    class FeedViewHolder(val binding: FeedItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var cardItem: ConstraintLayout = binding.feedItemCard
        fun bindData(feed: Feed) {
            binding.url = feed.urls.regular

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder(
            FeedItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bindData(getItem(position)!!)

        holder.cardItem.setOnClickListener {
            onClick.invoke(getItem(position), false)
        }

        holder.cardItem.setOnLongClickListener {
            onClick.invoke(getItem(position), true)
            return@setOnLongClickListener true
        }

    }

    class FeedDiffUtil : DiffUtil.ItemCallback<Feed>() {
        override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return areItemsTheSame(oldItem, newItem)
        }
    }
}