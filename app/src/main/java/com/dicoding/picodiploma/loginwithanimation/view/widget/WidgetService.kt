package com.dicoding.picodiploma.loginwithanimation.view.widget

import android.content.Intent
import android.widget.RemoteViewsService

class WidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return StoryRemoteViewsFactory(this.applicationContext)
    }
}
