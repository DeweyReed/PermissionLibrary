package com.finalweek10.permission.ui.main.app

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.finalweek10.permission.R
import com.finalweek10.permission.data.helper.PreferenceHelper
import com.finalweek10.permission.data.model.ShowConfig
import com.finalweek10.permission.data.model.VisibleApp
import com.finalweek10.permission.data.source.PermRepository
import com.finalweek10.permission.di.ActivityScoped
import com.finalweek10.permission.extension.isPackageEnabled
import com.finalweek10.permission.extension.sort
import com.finalweek10.permission.ui.main.MainContract
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject


/**
 * Created on 2017/9/14.
 */
@ActivityScoped
class AppsPresenter @Inject constructor(
        private val mPermRepository: PermRepository,
        private val mPrefHelper: PreferenceHelper) : MainContract.AppsPresenter {

    private var mView: MainContract.AppsView? = null

    private var mFirstLoad = true

    private var mUpdateDisposable: Disposable? = null
    private var mDisposable: Disposable? = null

    override fun takeView(view: MainContract.AppsView) {
        mView = view
    }

    override fun dropView() {
        mView = null
        mDisposable?.dispose()
        mFirstLoad = true
    }

    override fun updateApplications(context: Context, force: Boolean) {
        mUpdateDisposable?.dispose()
        mUpdateDisposable = Observable.fromCallable(
                mPermRepository.updateDatabase(context, force))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it) {
                        mView?.updateAllContent()
                    }
                }
    }

    override fun loadApplications(context: Context, groupId: Int, force: Boolean) {
        if (force || mFirstLoad) {
            mView?.showLoadingView()
            mDisposable?.dispose()
            mDisposable = Observable.fromCallable(
                    mPermRepository.getApplicationsAndTheirPermissionCountWithGroupId(groupId))
                    .map {
                        // disabled app check
                        // first handle special case
                        val pm = context.packageManager
                        mPrefHelper.showConfig.apply {
                            if (!normal && !system && disabled) {
                                val iterator = it.iterator()
                                while (iterator.hasNext()) {
                                    val item = iterator.next()
                                    if (pm.isPackageEnabled(item.first.packageName)) {
                                        iterator.remove()
                                    }
                                }
                                return@map it
                            }
                        }
                        // if not, we handle normal case
                        val showDisabled = mPrefHelper.showConfig.disabled
                        if (showDisabled) return@map it
                        val iterator = it.iterator()
                        while (iterator.hasNext()) {
                            val item = iterator.next()
                            if (!pm.isPackageEnabled(item.first.packageName)) {
                                iterator.remove()
                            }
                        }
                        it
                    }
                    .map {
                        // convert db app format to visible format
                        val apps = arrayListOf<VisibleApp>()
                        it.forEach { (app, permsCount) ->
                            apps.add(VisibleApp(app._id,
                                    app.packageName, app.label,
                                    app.versionCode, app.versionName,
                                    app.isSystem, app.more,
                                    null,
                                    permsCount))
                        }
                        apps.sort(mPrefHelper.usePinYin)
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        mView?.run {
                            showApplications(it)
                            hideLoadingView()
                        }
                    }
        }
        mFirstLoad = false
    }

//    override fun isShowingNotification(): Boolean = mPrefHelper.isShowingNotification
//
//    override fun setShowingNotification(value: Boolean) {
//        mPrefHelper.isShowingNotification = value
//    }

    override fun usePinYin(): Boolean = mPrefHelper.usePinYin

    override fun showConfigDialog(context: Context) {
        val originalConfig = mPrefHelper.showConfig.toIndices()
        MaterialDialog.Builder(context)
                .autoDismiss(false)
                .cancelable(true)
                .title(R.string.action_filter)
                .items(R.array.filter_items)
                .itemsCallbackMultiChoice(mPrefHelper.showConfig.toIndices()) { dialog, which, _ ->
                    if (which == null || Arrays.equals(originalConfig, which)) {
                        dialog.dismiss()
                        return@itemsCallbackMultiChoice false
                    } else if (which.isEmpty()) {
                        return@itemsCallbackMultiChoice false
                    }
                    mPrefHelper.showConfig = ShowConfig.Builder().indices(which).build()
                    mView?.updateAllContent()
                    dialog.dismiss()
                    true
                }
                .positiveText(android.R.string.ok)
                .show()
    }
}