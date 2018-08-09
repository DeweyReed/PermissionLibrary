package com.finalweek10.permission.ui.main

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.finalweek10.permission.R
import com.finalweek10.permission.ui.main.apk.ApkFragment
import com.finalweek10.permission.ui.main.app.AppsFragment
import com.finalweek10.permission.ui.main.group.GroupsFragment

/**
 * Created on 2017/9/14.
 */

class MainViewPagerAdapter(
        private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> AppsFragment.instance()
        1 -> GroupsFragment.instance()
        2 -> ApkFragment.instance()
        else -> throw IllegalStateException("No fragment for position $position")
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> context.getString(R.string.pager_title_app)
        1 -> context.getString(R.string.pager_title_group)
        2 -> context.getString(R.string.pager_title_apk)
        else -> throw IllegalStateException("No fragment title for position $position")
    }
}