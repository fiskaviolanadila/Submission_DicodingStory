package com.dicoding.picodiploma.loginwithanimation.view.widget

import android.content.Context
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.model.Story
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StoryRemoteViewsFactory(
    private val context: Context
) : RemoteViewsService.RemoteViewsFactory {

    private var stories: List<Story> = listOf()
    private val storyRepository: StoryRepository = Injection.provideStoryRepository()
    private var authToken: String? = getTokenFromSharedPreferences(context)

    override fun onCreate() {}

    override fun onDataSetChanged() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (authToken != null) {
                    val response = storyRepository.getStories("Bearer $authToken")

                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            stories = body.listStory.map {
                                Story(
                                    id = it.id,
                                    name = it.name,
                                    description = it.description,
                                    photoUrl = it.photoUrl,
                                    createdAt = it.createdAt,
                                    lon = it.lon,
                                    lat = it.lat
                                )
                            }
                        }
                    } else {
                        Log.e("StoryRemoteViewsFactory", "Failed to fetch stories. Error: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("StoryRemoteViewsFactory", "Error fetching stories", e)
            }
        }
    }

    override fun onDestroy() {}

    override fun getCount(): Int {
        return stories.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val story = stories[position]
        val remoteView = RemoteViews(context.packageName, R.layout.widget_story_item)

        remoteView.setTextViewText(R.id.widget_story_title, story.name)
        remoteView.setTextViewText(R.id.widget_story_description, story.description)
        remoteView.setImageViewResource(R.id.widget_story_image, R.drawable.ic_placeholder)

        val intent = Intent()
        intent.putExtra("story_id", story.id)

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        remoteView.setOnClickPendingIntent(R.id.widget_list_view, pendingIntent)

        return remoteView
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getLoadingView(): RemoteViews {
        val remoteView = RemoteViews(context.packageName, R.layout.widget_loading_item)
        return remoteView
    }

    private fun getTokenFromSharedPreferences(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", null)
    }
}
