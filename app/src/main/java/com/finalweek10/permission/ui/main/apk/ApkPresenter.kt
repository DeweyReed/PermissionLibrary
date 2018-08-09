package com.finalweek10.permission.ui.main.apk

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import com.finalweek10.permission.R
import com.finalweek10.permission.data.helper.PreferenceHelper
import com.finalweek10.permission.data.model.ApkFile
import com.finalweek10.permission.data.model.VisibleApp
import com.finalweek10.permission.data.source.PermRepository
import com.finalweek10.permission.di.ActivityScoped
import com.finalweek10.permission.extension.APK_INTENT_TYPE
import com.finalweek10.permission.ui.main.MainContract
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.apache.commons.io.FileUtils
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import java.io.File
import javax.inject.Inject


/**
 * Created on 2017/9/14.
 */
@ActivityScoped
class ApkPresenter @Inject constructor(
        private val mPermRepository: PermRepository,
        private val mPrefHelper: PreferenceHelper) : MainContract.ApkPresenter {

    private var mView: MainContract.ApkView? = null

    private var mFirstLoad = true
    private var mApkSelectedPosition = RecyclerView.NO_POSITION

    private var mDisposable: Disposable? = null

    override fun takeView(view: MainContract.ApkView) {
        mView = view
    }

    override fun dropView() {
        mView = null
        mDisposable?.dispose()
        mFirstLoad = true
    }

    override fun loadApks(context: Context, force: Boolean) {
        if (force || mFirstLoad) {
            mView?.apply {
                hideOptionLayout()
                showLoadingView()
            }
            mDisposable?.dispose()
            mDisposable = Observable.fromCallable(
                    mPermRepository.getAllStoredApks(context))
                    .map {
                        val info = arrayListOf<Pair<ApkFile, VisibleApp>>()
                        it.forEach { (apk, app) ->
                            info.add(Pair(apk, VisibleApp(app._id, app.packageName, app.label,
                                    app.versionCode, app.versionName,
                                    app.isSystem, app.more, apk.icon, apk.permCount)))
                        }
                        info
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        mView?.run {
                            showApks(it)
                            hideLoadingView()
                        }
                    }
        }
        mFirstLoad = false
    }

    override fun pickApk(fragment: ApkFragment) {
        fragment.context?.run {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = APK_INTENT_TYPE
                putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            }
            if (intent.resolveActivity(packageManager) != null) {
                fragment.startActivityForResult(intent, ApkFragment.REQUEST_CODE_APK_PICK)
            } else {
                toast(R.string.fail_to_pick_apk)
            }
        }
    }

    override fun setSelectedPosition(value: Int) {
        mApkSelectedPosition = value
    }

    override fun deleteApk(context: Context, path: String) {
        with(File(path)) {
            if (exists() && FileUtils.deleteQuietly(this)) {
                context.toast(R.string.delete_apk_ok)
                mPrefHelper.isApkDeleted = true
                apkDeletionCheck()
            } else {
                context.longToast(R.string.delete_apk_fails)
            }
        }
    }

    override fun apkDeletionCheck() {
        if (mPrefHelper.isApkDeleted && mApkSelectedPosition != RecyclerView.NO_POSITION) {
            mPrefHelper.isApkDeleted = false
            mView?.removeSelectedPosition(mApkSelectedPosition)
            mApkSelectedPosition = RecyclerView.NO_POSITION
        }
    }

    override fun handleActivityResult(context: Context, requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == RESULT_OK) {
            val (apk, app) = mPermRepository.getApkFromUri(context, data.data).call()
            mView?.openPickedApkDetail(Pair(apk, VisibleApp(app._id, app.packageName, app.label,
                    app.versionCode, app.versionName,
                    app.isSystem, app.more, apk.icon, apk.permCount)))
        }
    }
}