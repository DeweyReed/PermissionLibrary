package com.finalweek10.permission.ui.groupsapp

import android.annotation.SuppressLint
import android.os.Bundle
import com.finalweek10.permission.R
import com.finalweek10.permission.data.model.VisibleGroup
import com.finalweek10.permission.extension.addFragmentToActivity
import com.finalweek10.permission.extension.requiresLOrLater
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_groups_app.*
import javax.inject.Inject

class GroupsAppActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var group: VisibleGroup

    companion object {
        const val EXTRA_GROUP = "EXTRA_GROUP"
    }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups_app)

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = "${group.descriptionName}(${group.appCount})"
        }

        requiresLOrLater {
            supportStartPostponedEnterTransition()
        }

        if (savedInstanceState == null) {
            addFragmentToActivity(R.id.appsFragmentFrame, GroupsAppFragment.instance())
        }
    }
}
