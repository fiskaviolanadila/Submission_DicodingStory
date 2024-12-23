package com.dicoding.picodiploma.loginwithanimation.view.story

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.data.paging.LoadingStateAdapter
import com.dicoding.picodiploma.loginwithanimation.data.paging.StoryPagingAdapter
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityStoryBinding
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.view.maps.MapsActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding
    private val viewModel: StoryViewModel by viewModels {
        StoryViewModelFactory(Injection.provideStoryRepository())
    }

    private lateinit var userPreference: UserPreference
    private val pagingAdapter = StoryPagingAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(dataStore)

        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            performLogout()
        }

        binding.btnMaps.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        observePagingData()
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            val userSession = userPreference.getSession().first()
            if (userSession.token.isNotEmpty()) {
                showLoadingAndLoadStories(userSession.token)
                loadPagedStories(userSession.token)
            } else {
                navigateToWelcome()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val userSession = userPreference.getSession().first()
            if (userSession.token.isNotEmpty()) {
                showLoadingAndLoadStories(userSession.token)
                loadPagedStories(userSession.token)
            } else {
                navigateToWelcome()
            }
        }
    }

    private fun showLoadingAndLoadStories(token: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed({
            loadStories(token)
        }, 800)
    }

    private fun loadStories(token: String) {
        viewModel.getStories("Bearer $token")

        viewModel.storyList.observe(this) { stories ->
            if (stories.isNotEmpty()) {
                val adapter = StoryAdapter(stories) { story ->
                    val intent = Intent(this, StoryDetailActivity::class.java)
                    intent.putExtra("story_id", story.id)
                    startActivity(intent)
                }
                binding.recyclerView.layoutManager = LinearLayoutManager(this)
                binding.recyclerView.adapter = adapter
            } else {
                Toast.makeText(this, "No stories available", Toast.LENGTH_SHORT).show()
            }

            binding.progressBar.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = pagingAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter { pagingAdapter.retry() }
        )

        pagingAdapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> println("LoadState: Refresh is Loading")
                is LoadState.NotLoading -> println("LoadState: Refresh is NotLoading")
                is LoadState.Error -> println("LoadState: Refresh has Error ${(loadState.refresh as LoadState.Error).error}")
            }

            when (loadState.append) {
                is LoadState.Loading -> println("LoadState: Append is Loading")
                is LoadState.NotLoading -> println("LoadState: Append is NotLoading")
                is LoadState.Error -> println("LoadState: Append has Error ${(loadState.append as LoadState.Error).error}")
            }

            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            binding.recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
        }
    }

    private fun loadPagedStories(token: String) {
        if (token.isBlank()) {
            Toast.makeText(applicationContext, "Token is missing", Toast.LENGTH_SHORT).show()
            return
        }

        val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

        viewModel.getStoriesPaging(formattedToken).observe(this) { pagingData ->
            println("Submitting Paging Data to Adapter.")
            pagingAdapter.submitData(lifecycle, pagingData)
        }
    }

    private fun observePagingData() {
        lifecycleScope.launch {
            val token = "Bearer ${userPreference.getSession().first().token}"
            println("Fetching Paging Data with Token: $token")

            viewModel.getStoriesPaging(token).distinctUntilChanged().observe(this@StoryActivity) { pagingData ->
                pagingAdapter.submitData(lifecycle, pagingData)
            }
        }
    }

    private fun performLogout() {
        lifecycleScope.launch {
            userPreference.logout()
            navigateToWelcome()
        }
    }

    private fun navigateToWelcome() {
        val intent = Intent(this@StoryActivity, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("finishAffinity()"))
    override fun onBackPressed() {
        finishAffinity()
    }
}
