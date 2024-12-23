package com.dicoding.picodiploma.loginwithanimation.view.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.view.story.StoryActivity

class WidgetProvider : android.appwidget.AppWidgetProvider() {

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        appWidgetIds?.forEach { appWidgetId ->
            val intent = Intent(context, WidgetService::class.java)
            val views = RemoteViews(context?.packageName, R.layout.widget_story_list)
            views.setRemoteAdapter(R.id.widget_list_view, intent)

            val token = getTokenFromSharedPreferences(context)

            val openAppIntent = Intent(context, StoryActivity::class.java).apply {
                putExtra("AUTH_TOKEN", token)
            }
            val openAppPendingIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            views.setPendingIntentTemplate(R.id.widget_list_view, openAppPendingIntent)

            appWidgetManager?.updateAppWidget(appWidgetId, views)
        }
    }

    private fun getTokenFromSharedPreferences(context: Context?): String? {
        val sharedPreferences = context?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences?.getString("auth_token", null)
    }
}
