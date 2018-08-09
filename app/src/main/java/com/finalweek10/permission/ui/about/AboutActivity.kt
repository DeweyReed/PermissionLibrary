package com.finalweek10.permission.ui.about

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.finalweek10.permission.R
import xyz.aprildown.about.AboutBuilder

/**
 * Created on 2017/9/26.
 */

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = AboutBuilder.with(this)
                .setName("DeweyReed")
                .setSubTitle("Android Developer")
                .setBrief("Keeping building better app")

                .setAppIcon(R.mipmap.ic_launcher)
                .setAppName(R.string.app_name)
                .setVersionNameAsAppSubTitle()

                .addUpdateAction()
                .addFiveStarsAction()
                .addShareAction(R.string.app_name)

                .setWrapScrollView(true)
                .setLinksAnimated(true)
                .setShowAsCard(!resources.getBoolean(R.bool.is_tablet))
                .build()

        setContentView(view)
    }
}