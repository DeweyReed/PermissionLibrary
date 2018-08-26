package com.finalweek10.permission.ui.groupsapp

import android.app.Activity
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
import butterknife.Unbinder
import com.finalweek10.permission.R
import com.finalweek10.permission.data.model.VisibleApp
import com.finalweek10.permission.data.model.VisibleGroup
import com.finalweek10.permission.di.ActivityScoped
import com.finalweek10.permission.ui.detail.AppDetailActivity
import com.finalweek10.permission.ui.main.MainContract
import com.finalweek10.permission.ui.main.app.AppAdapter
import com.finalweek10.permission.ui.view.FunnyLoader
import dagger.android.support.DaggerFragment
import javax.inject.Inject

/**
 * Created on 2017/9/14.
 */

@Suppress("MemberVisibilityCanPrivate")
@ActivityScoped
class GroupsAppFragment @Inject constructor() : DaggerFragment(),
        MainContract.AppsView {

    @Inject
    lateinit var mPresenter: MainContract.AppsPresenter

    @BindView(R.id.appsRecyclerView)
    lateinit var mRecyclerView: RecyclerView
    private lateinit var mAppsAdapter: AppAdapter

    @BindView(R.id.emptyView)
    lateinit var mEmptyView: View
    @BindView(R.id.funnyLoader)
    lateinit var mFunnyLoader: FunnyLoader
    @BindView(R.id.refresh)
    lateinit var refresh: SwipeRefreshLayout

    private lateinit var mUnBinder: Unbinder

    @Inject
    lateinit var group: VisibleGroup

    companion object {
        fun instance() = GroupsAppFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)
                ?: return null
        mUnBinder = ButterKnife.bind(this, rootView)
        mAppsAdapter = AppAdapter(context!!, mPresenter.usePinYin()).apply {
            setHasStableIds(true)
            setOnItemClickListener { adapter, viewClicked, position ->
                context?.run {
                    val v = viewClicked.findViewById<View>(R.id.appIcon)
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            activity as Activity, v, ViewCompat.getTransitionName(v) ?: "")
                    val intent = Intent(context, AppDetailActivity::class.java)
                    intent.putExtra(AppDetailActivity.EXTRA_APP, adapter.getItem(position) as VisibleApp)
                    ActivityCompat.startActivity(this, intent, options.toBundle())
                }
            }
        }
        mRecyclerView.adapter = mAppsAdapter

        mRecyclerView.addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        mAppsAdapter.setEmptyView(R.layout.layout_empty, mRecyclerView)
        refresh.isEnabled = false
        return rootView
    }

    override fun onResume() {
        super.onResume()
        mPresenter.takeView(this)
        context?.run { mPresenter.loadApplications(this, group._id, false) }
    }

    override fun onDestroyView() {
        mPresenter.dropView()
        mUnBinder.unbind()
        super.onDestroyView()
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

    override fun showApplications(apps: List<VisibleApp>) {
        mAppsAdapter.replaceData(apps)
    }

    override fun isAlive(): Boolean = isAdded

    override fun updateAllContent() = Unit
}