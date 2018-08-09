package com.finalweek10.permission.ui.main

import android.content.Context
import android.content.Intent
import com.finalweek10.permission.data.model.ApkFile
import com.finalweek10.permission.data.model.VisibleApp
import com.finalweek10.permission.data.model.VisibleGroup
import com.finalweek10.permission.ui.BasePresenter
import com.finalweek10.permission.ui.BaseView
import com.finalweek10.permission.ui.main.apk.ApkFragment

/**
 * Created on 2017/9/14.
 */

interface MainContract {
    interface AppsView : BaseView {
        fun showLoadingView()
        fun hideLoadingView()

        fun showApplications(apps: List<VisibleApp>)
        fun updateAllContent()

        fun isAlive(): Boolean
    }

    interface AppsPresenter : BasePresenter<AppsView> {
        fun updateApplications(context: Context, force: Boolean)
        fun loadApplications(context: Context, groupId: Int, force: Boolean)

//        fun isShowingNotification(): Boolean
//        fun setShowingNotification(value: Boolean)

        fun showConfigDialog(context: Context)

        fun usePinYin(): Boolean
    }

    interface GroupsView : BaseView {
        fun showLoadingView()
        fun hideLoadingView()

        fun showGroups(groups: List<VisibleGroup>)

        fun isAlive(): Boolean
    }


    interface GroupsPresenter : BasePresenter<GroupsView> {
        fun loadGroups(context: Context, force: Boolean)
    }

    interface ApkView : BaseView {
        fun showLoadingView()
        fun hideLoadingView()
        fun hideOptionLayout()

        fun removeSelectedPosition(position: Int)
        fun showApks(apks: List<Pair<ApkFile, VisibleApp>>)

        fun openPickedApkDetail(pair: Pair<ApkFile, VisibleApp>)

        fun isAlive(): Boolean
    }

    interface ApkPresenter : BasePresenter<ApkView> {
        fun loadApks(context: Context, force: Boolean)

        fun pickApk(fragment: ApkFragment)

        fun setSelectedPosition(value: Int)
        fun deleteApk(context: Context, path: String)
        fun apkDeletionCheck()

        fun handleActivityResult(context: Context, requestCode: Int, resultCode: Int, data: Intent)
    }
}