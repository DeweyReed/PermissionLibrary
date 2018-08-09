package com.finalweek10.permission.ui.detail

import android.content.Context
import android.graphics.drawable.Drawable
import com.finalweek10.permission.data.model.SectionPerm
import com.finalweek10.permission.ui.BasePresenter
import com.finalweek10.permission.ui.BaseView

/**
 * Created on 2017/9/14.
 */

interface AppDetailContract {
    interface View : BaseView {
        fun showLoadingView()
        fun hideLoadingView()
        fun quitActivity()

        fun showApplication(perms: List<SectionPerm>)

        fun showPermDesp(value: Boolean)

        fun addCuteLabel(info: String, explanation: String = "")
    }

    interface Presenter : BasePresenter<View> {
        fun loadPermissions(context: Context)

        fun isApkFile(): Boolean
        fun getApkPath(): String
        fun findAppPackageNameAndLocation(context: Context): String
        fun findAppIcon(context: Context): Drawable

        fun shouldShowInstallAppMenuItem(): Boolean
        fun deleteApk(context: Context)
        fun installApp(context: Context)
        fun configureSystemPermission(context: Context)

        fun isShowingPermDesp(): Boolean
        fun setShowingPermDesp(value: Boolean)

        fun createLabels(context: Context)
    }
}