package com.finalweek10.permission.ui.detail

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import com.afollestad.materialdialogs.MaterialDialog
import com.finalweek10.permission.R
import com.finalweek10.permission.data.db.DatabaseUtil
import com.finalweek10.permission.data.helper.PreferenceHelper
import com.finalweek10.permission.data.model.*
import com.finalweek10.permission.data.source.PermRepository
import com.finalweek10.permission.di.ActivityScoped
import com.finalweek10.permission.extension.*
import com.finalweek10.permission.ui.view.createDeletionAssureDialog
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
class AppDetailPresenter @Inject constructor(
        private val mPermRepository: PermRepository,
        private val mPrefHelper: PreferenceHelper) : AppDetailContract.Presenter {

    @Inject
    lateinit var mVisibleApp: VisibleApp
    @Inject
    lateinit var mApkFile: ApkFile

    private var mView: AppDetailContract.View? = null

    private var isApkFile: Boolean = false

    private var mDisposable: Disposable? = null

    override fun takeView(view: AppDetailContract.View) {
        mView = view
        isApkFile = !mApkFile.isEmpty()
    }

    override fun dropView() {
        mView = null
        mDisposable?.dispose()
    }

    override fun shouldShowInstallAppMenuItem(): Boolean = isApkFile && !isNOrLater()

    override fun isApkFile(): Boolean = isApkFile

    override fun getApkPath(): String = mApkFile.path

    override fun findAppPackageNameAndLocation(context: Context): String = mVisibleApp.packageName + if (isApkFile)
        "\n\n" + context.getString(R.string.app_detail_apk_location_template, mApkFile.path) else ""

    override fun findAppIcon(context: Context): Drawable {
        return if (isApkFile) {
            val path = mApkFile.path
            val pm = context.packageManager
            val pi = pm.getPackageArchiveInfo(path, PackageManager.GET_META_DATA)
            val ai = pi?.applicationInfo
            if (ai != null) {
                ai.sourceDir = path
                ai.publicSourceDir = path
                ai.loadIcon(pm)
            } else context.drawable(R.drawable.ic_error)
        } else {
            context.getApplicationIcon(mVisibleApp.packageName)
        }
    }

    override fun loadPermissions(context: Context) {
        mView?.showLoadingView()
        mDisposable?.dispose()
        mDisposable = Observable.fromCallable(
                if (isApkFile) mPermRepository.getPermissionsWithApkFile(context, mApkFile)
                else mPermRepository.getPermissionsWithAppId(mVisibleApp._id))
                .map {
                    val map = LinkedHashMap<String, MutableList<VisiblePerm>>()
                    mPermRepository.getAllGroups().call().forEach { group ->
                        map[group.name] = mutableListOf()
                    }
                    it.forEach { (perm, group) ->
                        map[group.name]?.add(VisiblePerm(
                                perm._id, perm.name,
                                context.permissionToLabel(perm.name),
                                context.permissionToDescription(perm.name),
                                PERM_NONE))
                    }
                    map
                }
                .map {
                    // re sort other permission to different groups
                    val otherName = PermRepository.GROUP_OTHER.name
                    if (it.containsKey(otherName)) {
                        val otherPerms: List<VisiblePerm> = it[otherName]!!
                        it.remove(otherName)
                        val network = mutableListOf<VisiblePerm>()
                        val other1 = mutableListOf<VisiblePerm>()
                        val other = mutableListOf<VisiblePerm>()
                        val guess = mutableListOf<VisiblePerm>()
                        val launcher = mutableListOf<VisiblePerm>()
                        val removed = mutableListOf<VisiblePerm>()
                        otherPerms.forEach { perm ->
                            val name = perm.simpleName
                            when {
                                name in DatabaseUtil.networkPermissionList -> network.add(perm)
                                name in DatabaseUtil.other1PermissionList -> other1.add(perm)
                                name in DatabaseUtil.removedPermissionList -> removed.add(perm)
                                name.contains("launcher") -> launcher.add(perm)
                                else -> {
                                    if (TextUtils.isEmpty(perm.permLabel)
                                            && TextUtils.isEmpty(perm.permDescription)) {
                                        guess.add(perm)
                                    } else {
                                        other.add(perm)
                                    }
                                }
                            }
                        }
                        other.sortByDescending { item -> item.permLabel }
                        it.apply {
                            put(PermRepository.GROUP_NETWORK.name, network)
                            put(PermRepository.GROUP_OTHER1.name, other1)
                            put(PermRepository.GROUP_OTHER.name, other)
                            put(PermRepository.GROUP_LAUNCHER.name, launcher)
                            put(PermRepository.GROUP_GUESS.name, guess)
                            put(PermRepository.GROUP_REMOVED.name, removed)
                        }
                    }
                    it
                }
                .map {
                    val ls = arrayListOf<SectionPerm>()
                    val pm = context.packageManager
                    val packageName = mVisibleApp.packageName
                    it.keys.forEach { group ->
                        val size = it[group]?.size ?: 0
                        if (size > 0) {
                            val isDangerousGroup = group in DatabaseUtil.dangerousGroup
                            ls.add(SectionPerm(true,
                                    context.simpleGroupNameToDescription(group),
                                    size, isDangerousGroup))
                            it[group]?.forEach { holder ->
                                if (isDangerousGroup)
                                    holder.state = if (pm.checkPermission(
                                                    holder.simpleName.simplePermNameToUsesPermission(),
                                                    packageName) == PackageManager.PERMISSION_GRANTED)
                                        PERM_ON else PERM_OFF
                                ls.add(SectionPerm(holder))
                            }
                        }
                    }
                    ls
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mView?.run {
                        showApplication(it)
                        hideLoadingView()
                    }
                }
    }

    override fun deleteApk(context: Context) {
        context.createDeletionAssureDialog(MaterialDialog.SingleButtonCallback { _, _ ->
            with(File(mApkFile.path)) {
                if (exists() && FileUtils.deleteQuietly(this)) {
                    context.toast(R.string.delete_apk_ok)
                    mPrefHelper.isApkDeleted = true
                    mView?.quitActivity()
                } else {
                    context.longToast(R.string.delete_apk_fails)
                }
            }
        })
    }

    override fun installApp(context: Context) {
        val promptInstall = Intent(Intent.ACTION_VIEW)
                .setDataAndType(Uri.parse("file://${mApkFile.path}"), APK_INTENT_TYPE)
        context.startActivity(promptInstall)
    }

    override fun configureSystemPermission(context: Context) {
        val pm = context.packageManager
        val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.data = Uri.fromParts("package", mVisibleApp.packageName, null)
        if (i.resolveActivity(pm) != null) {
            context.startActivity(i)
        } else {
            val i2 = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            i2.addCategory(Intent.CATEGORY_DEFAULT)
            if (i2.resolveActivity(pm) != null) {
                context.startActivity(i2)
            } else {
                context.longToast("No activity found! Please go to Setting->Apps")
            }
        }
    }

    override fun isShowingPermDesp(): Boolean = mPrefHelper.isShowingPermDesp

    override fun setShowingPermDesp(value: Boolean) {
        mPrefHelper.isShowingPermDesp = value
        mView?.showPermDesp(value)
    }

    override fun createLabels(context: Context) {
        mView?.let {
            it.addCuteLabel(context.resources.getQuantityString(R.plurals.list_item_perm_count,
                    mVisibleApp.permissionCount, mVisibleApp.permissionCount))
            if (mVisibleApp.isSystem) {
                it.addCuteLabel(context.getString(R.string.label_system),
                        context.getString(R.string.label_system_explanation))
            }
            if (!context.packageManager.isPackageEnabled(mVisibleApp.packageName)) {
                it.addCuteLabel(context.getString(R.string.label_disabled))
            }
        }
    }
}