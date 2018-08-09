package com.finalweek10.permission.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.finalweek10.permission.R
import com.finalweek10.permission.data.helper.PreferenceHelper
import com.finalweek10.permission.data.helper.RateHelper
import com.finalweek10.permission.data.helper.UpdateHelper
import com.finalweek10.permission.data.source.PermRepository
import com.finalweek10.permission.extension.startActivityWithExplode
import com.finalweek10.permission.ui.about.AboutActivity
import com.finalweek10.permission.ui.main.app.AppsPresenter
import com.finalweek10.permission.ui.main.group.GroupsPresenter
import com.finalweek10.permission.ui.view.createHelpDialog
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@Suppress("MemberVisibilityCanPrivate")
class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var mAppsPresenter: AppsPresenter

    @Inject
    lateinit var mGroupsPresenter: GroupsPresenter

    @Inject
    lateinit var mPrefHelper: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(false)
        }

        mainViewPager.adapter = MainViewPagerAdapter(this, supportFragmentManager)
        tabLayout.setupWithViewPager(mainViewPager)

        RateHelper(mPrefHelper).rateAdvice(this)
        UpdateHelper(mPrefHelper).update(this)

        mAppsPresenter.updateApplications(this, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

//    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
//        menu?.apply {
//            findItem(R.id.action_set_notification)?.isChecked =
//                    mAppsPresenter.isShowingNotification()
//            return true
//        }
//        return false
//    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when (id) {
            R.id.action_show_config -> {
                mAppsPresenter.showConfigDialog(this)
                return true
            }
//            R.id.action_set_notification -> {
//                val isChecked = !item.isChecked
//                item.isChecked = isChecked
//                mAppsPresenter.setShowingNotification(isChecked)
//                toast(if (isChecked) R.string.notify_setting_on else R.string.notify_setting_off)
//                return true
//            }
            R.id.action_help -> {
                createHelpDialog()
                return true
            }
            R.id.action_about -> {
                startActivityWithExplode(AboutActivity::class.java)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun updateAllContent() {
        mAppsPresenter.loadApplications(this, PermRepository.NO_ID, true)
        mGroupsPresenter.loadGroups(this, true)
    }
}
