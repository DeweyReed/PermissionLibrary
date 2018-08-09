package com.finalweek10.permission.ui.main.apk

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.afollestad.materialdialogs.MaterialDialog
import com.finalweek10.permission.R
import com.finalweek10.permission.data.model.ApkFile
import com.finalweek10.permission.data.model.VisibleApp
import com.finalweek10.permission.di.ActivityScoped
import com.finalweek10.permission.ui.detail.AppDetailActivity
import com.finalweek10.permission.ui.main.MainContract
import com.finalweek10.permission.ui.view.FunnyLoader
import com.finalweek10.permission.ui.view.createDeletionAssureDialog
import dagger.android.support.DaggerFragment
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

/**
 * Created on 2017/9/14.
 */

@Suppress("MemberVisibilityCanPrivate")
@ActivityScoped
class ApkFragment @Inject constructor() : DaggerFragment(), MainContract.ApkView {

    @Inject
    lateinit var mPresenter: MainContract.ApkPresenter

    @BindView(R.id.appsRecyclerView)
    lateinit var mRecyclerView: RecyclerView

    private lateinit var mAppAdapter: ApkAdapter

    @BindView(R.id.emptyView)
    lateinit var mLoadingView: View
    @BindView(R.id.funnyLoader)
    lateinit var mFunnyLoader: FunnyLoader
    @BindView(R.id.optionsLayout)
    lateinit var mOptionsLayout: ViewGroup
    @BindView(R.id.refresh)
    lateinit var refresh: SwipeRefreshLayout

    private lateinit var mUnBinder: Unbinder

    companion object {
        fun instance() = ApkFragment()
        internal const val REQUEST_CODE_APK_PICK = 0

        private const val PERM_REQUEST_CODE_SCAN = 10
        private const val PERM_REQUEST_CODE_PICK = 11
        private const val PERM_REQUEST_CODE_DELETE = 20
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_apk, container, false) ?: return null
        mUnBinder = ButterKnife.bind(this, rootView)
        mAppAdapter = ApkAdapter().apply {
            setHasStableIds(true)
            setOnItemClickListener { adapter, viewClicked, position ->
                mPresenter.setSelectedPosition(position)
                context?.run {
                    val v = viewClicked.findViewById<View>(R.id.appIcon)
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            activity as Activity, v, ViewCompat.getTransitionName(v))
                    val intent = Intent(context, AppDetailActivity::class.java)
                    val pair = adapter.getItem(position) as Pair<*, *>
                    intent.putExtra(AppDetailActivity.EXTRA_APP, pair.second as VisibleApp)
                            .putExtra(AppDetailActivity.EXTRA_APK_FILE, pair.first as ApkFile)
                    ActivityCompat.startActivity(this, intent, options.toBundle())
                }
            }
            setOnItemLongClickListener { adapter, _, position ->
                mPresenter.setSelectedPosition(position)
                onDeleteApk(((adapter.getItem(position) as Pair<*, *>).first as ApkFile).path)
                true
            }
        }
        mRecyclerView.adapter = mAppAdapter

        mRecyclerView.addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        mAppAdapter.setEmptyView(R.layout.layout_empty, mRecyclerView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        refresh.setOnRefreshListener {
            refresh.isRefreshing = false
            loadApks(view.context, true)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this)
    }

    @Suppress("unused")
    @AfterPermissionGranted(PERM_REQUEST_CODE_SCAN)
    @OnClick(R.id.searchApkBtn)
    fun onSearchBtnClick() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val context = context ?: return
        if (EasyPermissions.hasPermissions(context, permission)) {
            loadApks(context, false)
        } else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.permission_external_storage),
                    PERM_REQUEST_CODE_SCAN, permission)
        }
    }

    private fun loadApks(context: Context, force: Boolean) {
        context.run { mPresenter.loadApks(this, force) }
    }

    @Suppress("unused")
    @AfterPermissionGranted(PERM_REQUEST_CODE_PICK)
    @OnClick(R.id.pickApkBtn)
    fun onPickBtnClick() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val context = context ?: return
        if (EasyPermissions.hasPermissions(context, permission)) {
            mPresenter.pickApk(this)
        } else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.permission_external_storage),
                    PERM_REQUEST_CODE_PICK, permission)
        }
    }

    private fun onDeleteApk(path: String) {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val context = context ?: return
        if (EasyPermissions.hasPermissions(context, permission)) {
            context.createDeletionAssureDialog(MaterialDialog.SingleButtonCallback { _, _ ->
                mPresenter.deleteApk(context, path)
            })
        } else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.permission_delete_apk),
                    PERM_REQUEST_CODE_DELETE, permission)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && context != null)
            mPresenter.handleActivityResult(context!!, requestCode, resultCode, data)
    }

    override fun openPickedApkDetail(pair: Pair<ApkFile, VisibleApp>) {
        startActivity(Intent(context, AppDetailActivity::class.java).apply {
            putExtra(AppDetailActivity.EXTRA_APP, pair.second)
            putExtra(AppDetailActivity.EXTRA_APK_FILE, pair.first)
            putExtra(AppDetailActivity.EXTRA_BACK_STACK, false)
        })
    }

    override fun onResume() {
        super.onResume()
        mPresenter.run {
            takeView(this@ApkFragment)
            apkDeletionCheck()
        }
    }

    override fun onDestroyView() {
        mPresenter.dropView()
        mUnBinder.unbind()
        super.onDestroyView()
    }

    override fun showLoadingView() {
        mLoadingView.animate()
                .withStartAction {
                    mLoadingView.apply {
                        scaleY = 0f
                        visibility = View.VISIBLE
                    }
                }
                .scaleY(1f)
                .setDuration(100)
                .start()

        mFunnyLoader.start()
        refresh.visibility = View.GONE
    }

    override fun hideLoadingView() {
        mLoadingView.visibility = View.GONE
        mFunnyLoader.stop()
        refresh.visibility = View.VISIBLE
    }

    override fun hideOptionLayout() {
        mOptionsLayout.animate().scaleX(0f).scaleY(0f)
                .alpha(0f).setDuration(250)
                .withEndAction { mOptionsLayout.visibility = View.GONE }
                .start()
    }

    override fun showApks(apks: List<Pair<ApkFile, VisibleApp>>) {
        mAppAdapter.replaceData(apks)
    }

    override fun removeSelectedPosition(position: Int) {
        mAppAdapter.remove(position)
    }

    override fun isAlive(): Boolean = isAdded
}