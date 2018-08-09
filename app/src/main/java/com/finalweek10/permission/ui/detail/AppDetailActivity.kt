package com.finalweek10.permission.ui.detail

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.transition.Transition
import android.view.Menu
import android.view.MenuItem
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import com.finalweek10.permission.R
import com.finalweek10.permission.data.model.SectionPerm
import com.finalweek10.permission.data.model.VisibleApp
import com.finalweek10.permission.di.ActivityScoped
import com.finalweek10.permission.extension.isLOrLater
import com.finalweek10.permission.extension.isMOrLater
import com.finalweek10.permission.ui.view.FunnyLoader
import com.finalweek10.permission.ui.view.createCuteLabel
import com.finalweek10.permission.ui.view.createLabelExplanationDialog
import com.finalweek10.permission.ui.view.createPermissionItemItemActionDialog
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_app_detail.*
import kotlinx.android.synthetic.main.content_app_detail.*
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@Suppress("MemberVisibilityCanPrivate")
@ActivityScoped
class AppDetailActivity : DaggerAppCompatActivity(),
        AppDetailContract.View {

    @Inject
    lateinit var mVisibleApp: VisibleApp

    @Inject
    lateinit var mHasBackStack: AtomicBoolean

    @Inject
    lateinit var mPresenter: AppDetailContract.Presenter

    @BindView(R.id.listAppPermissions)
    lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AppDetailAdapter

    @BindView(R.id.emptyView)
    lateinit var mEmptyView: View
    @BindView(R.id.funnyLoader)
    lateinit var mFunnyLoader: FunnyLoader

    companion object {
        const val EXTRA_APP = "EXTRA_APP"
        const val EXTRA_BACK_STACK = "EXTRA_BACK_STACK"
        const val EXTRA_APK_FILE = "EXTRA_APK_FILE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_detail)

        setUpToolbar()
        setUpViews()
        setUpEnterTransition()
        setUpPresenter()
        populateInfo()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_detail, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.run {
            findItem(R.id.action_show_desp)?.isChecked = mPresenter.isShowingPermDesp()
            findItem(R.id.action_install_app)?.isVisible = mPresenter.shouldShowInstallAppMenuItem()
            findItem(R.id.action_delete_apk)?.isVisible = mPresenter.isApkFile()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when (id) {
            R.id.action_app_setting -> {
                mPresenter.configureSystemPermission(this)
                return true
            }
            R.id.action_delete_apk -> {
                onDeleteApk()
                return true
            }
            R.id.action_install_app -> {
                mPresenter.installApp(this)
                return true
            }
            R.id.action_show_desp -> {
                val isChecked = !item.isChecked
                item.isChecked = isChecked
                mPresenter.setShowingPermDesp(isChecked)
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        mPresenter.loadPermissions(this@AppDetailActivity)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this)
    }

    override fun onBackPressed() {
        mRecyclerView.animate().alpha(0f).setDuration(100).start()
        super.onBackPressed()
    }

    private fun setUpPresenter() {
        mPresenter.run {
            takeView(this@AppDetailActivity)
            mAdapter.showState = !isApkFile() && isMOrLater()
            createLabels(this@AppDetailActivity)
        }
    }

    override fun onDestroy() {
        mPresenter.dropView()
        super.onDestroy()
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = mVisibleApp.label
        }
    }

    private fun setUpViews() {
        ButterKnife.bind(this)
        mAdapter = AppDetailAdapter().apply {
            showDesp = mPresenter.isShowingPermDesp()
            setHasStableIds(true)
            setOnItemChildClickListener { _, view, _ ->
                val id = view.id
                if (id == R.id.itemHeaderText) {
                    createLabelExplanationDialog(getString(R.string.label_dangerous_permission),
                            getString(R.string.label_dangerous_permission_explanation))
                } else if (id == R.id.itemState) {
                    mPresenter.configureSystemPermission(this@AppDetailActivity)
                }
            }
            setOnItemChildLongClickListener { _, view, position ->
                if (view?.id == R.id.layoutItemAppPerm) {
                    mAdapter.getItem(position)?.t?.let { createPermissionItemItemActionDialog(it) }
                    true
                } else {
                    false
                }
            }
        }
        mRecyclerView.adapter = mAdapter
        mRecyclerView.addItemDecoration(
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mAdapter.setEmptyView(R.layout.layout_empty, mRecyclerView)
    }

    private fun populateInfo() {
        collapsingToolbarLayout.title = mVisibleApp.label
        appIcon.setImageDrawable(mPresenter.findAppIcon(this))
        appPackageName.text = mPresenter.findAppPackageNameAndLocation(this)
        if (mPresenter.isApkFile()) {
            appPackageName.setOnClickListener {
                val selectedUri = Uri.parse(mPresenter.getApkPath()
                        .substringBeforeLast("/"))
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(selectedUri, "resource/folder")
                if (intent.resolveActivityInfo(packageManager, 0) != null) {
                    try {
                        startActivity(intent)
                    } catch (t: Throwable) {
                    }
                }
            }
        }
        appVersionInfo.text = getString(R.string.app_detail_version_template,
                mVisibleApp.versionCode, mVisibleApp.versionName)
    }

    @SuppressLint("NewApi")
    private fun setUpEnterTransition() {
        if (!mHasBackStack.get()) {
            showContentWithAnimation()
            return
        }

        if (isLOrLater()) {
            supportStartPostponedEnterTransition()

            window.sharedElementEnterTransition.addListener(object : Transition.TransitionListener {
                override fun onTransitionEnd(p0: Transition?) {
                    p0?.removeListener(this)
                    showContentWithAnimation()
                }

                override fun onTransitionResume(p0: Transition?) {
                }

                override fun onTransitionPause(p0: Transition?) {
                }

                override fun onTransitionCancel(p0: Transition?) {
                }

                override fun onTransitionStart(p0: Transition?) {
                }
            })
        } else {
            showContentWithAnimation()
        }
    }

    private fun showContentWithAnimation() {
        contentLayout.visibility = View.VISIBLE
        contentLayout.alpha = 0f
        contentLayout.animate().alpha(1f).setDuration(100).start()
    }

    override fun showLoadingView() {
        mEmptyView.visibility = View.VISIBLE
        mFunnyLoader.start()
        mRecyclerView.visibility = View.GONE
    }

    override fun hideLoadingView() {
        mEmptyView.visibility = View.GONE
        mFunnyLoader.stop()
        mRecyclerView.visibility = View.VISIBLE
    }

    override fun quitActivity() {
        onBackPressed()
    }

    override fun showApplication(perms: List<SectionPerm>) {
        mAdapter.replaceData(perms)
    }

    override fun showPermDesp(value: Boolean) {
        mAdapter.showDesp = value
    }

    override fun addCuteLabel(info: String, explanation: String) {
        val labelView = createCuteLabel(info)
        if (!TextUtils.isEmpty(explanation)) {
            labelView.setOnClickListener {
                createLabelExplanationDialog(info, explanation)
            }
        }
        labelLayout.addView(labelView)
    }

    private fun onDeleteApk() {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (EasyPermissions.hasPermissions(this, permission)) {
            mPresenter.deleteApk(this)
        } else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.permission_delete_apk), 0, permission)
        }
    }
}
