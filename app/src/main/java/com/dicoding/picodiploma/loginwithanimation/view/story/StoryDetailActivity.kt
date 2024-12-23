package com.dicoding.picodiploma.loginwithanimation.view.story

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityStoryDetailBinding
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding
    private lateinit var viewModel: StoryDetailViewModel
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(dataStore)

        val storyId = intent.getStringExtra("story_id") ?: return
        val token = runBlocking {
            userPreference.getSession().first().token
        }

        if (token.isNotEmpty()) {
            setupViewModel()
            viewModel.getStoryDetail(storyId, "Bearer $token")

            viewModel.story.observe(this) { story ->
                story?.let {
                    binding.tvDetailName.text = it.name
                    binding.tvDetailDescription.text = it.description
                    binding.tvDetailCreatedAt.text = it.createdAt
                    Glide.with(this).load(it.photoUrl).into(binding.tvDetailPhoto)
                }
            }
        } else {
            finish()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            StoryDetailViewModelFactory(Injection.provideStoryRepository())
        )[StoryDetailViewModel::class.java]
    }
}
