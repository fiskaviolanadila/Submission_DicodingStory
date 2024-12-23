package com.dicoding.picodiploma.loginwithanimation.view.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.R

class ConfigureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configure)

        val appWidgetManager = AppWidgetManager.getInstance(this)
        val thisWidget = ComponentName(this, WidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

        finish()
    }
}
