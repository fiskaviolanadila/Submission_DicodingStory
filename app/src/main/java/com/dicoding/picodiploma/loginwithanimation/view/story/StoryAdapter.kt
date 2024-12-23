package com.dicoding.picodiploma.loginwithanimation.view.story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemStoryBinding
import com.dicoding.picodiploma.loginwithanimation.data.model.ListStoryItem

class StoryAdapter(
    private val stories: List<ListStoryItem>,
    private val onClick: (ListStoryItem) -> Unit
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = stories[position]
        holder.bind(story)
    }

    override fun getItemCount(): Int = stories.size

    inner class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClick(stories[position])
                }
            }
        }

        fun bind(story: ListStoryItem) {
            binding.apply {
                tvItemName.text = story.name
                tvItemDescription.text = story.description
                Glide.with(itemView.context).load(story.photoUrl).into(tvItemPhoto)
            }
        }
    }
}
