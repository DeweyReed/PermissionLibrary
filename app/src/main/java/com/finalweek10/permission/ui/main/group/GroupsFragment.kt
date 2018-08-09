package com.finalweek10.permission.ui.main.group

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
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.finalweek10.permission.R
import com.finalweek10.permission.data.model.VisibleGroup
import com.finalweek10.permission.di.ActivityScoped
import com.finalweek10.permission.ui.groupsapp.GroupsAppActivity
import com.finalweek10.permission.ui.main.MainContract
import com.finalweek10.permission.ui.view.FunnyLoader
import dagger.android.support.DaggerFragment
import javax.inject.Inject

@Suppress("MemberVisibilityCanPrivate")
@ActivityScoped
class GroupsFragment @Inject constructor() : DaggerFragment(),
        MainContract.GroupsView {

    @Inject
    lateinit var mPresenter: MainContract.GroupsPresenter

    @BindView(R.id.appsRecyclerView)
    lateinit var mRecyclerView: RecyclerView
    private lateinit var mGroupsAdapter: GroupAdapter

    @BindView(R.id.emptyView)
    lateinit var mLoadingView: View
    @BindView(R.id.funnyLoader)
    lateinit var mFunnyLoader: FunnyLoader
    @BindView(R.id.refresh)
    lateinit var refresh: SwipeRefreshLayout

    private lateinit var mUnBinder: Unbinder

    companion object {
        fun instance() = GroupsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false) ?: return null
        mUnBinder = ButterKnife.bind(this, rootView)
        mGroupsAdapter = GroupAdapter().apply {
            setHasStableIds(true)
            setOnItemClickListener { adapter, viewClicked, position ->
                context?.run {
                    val v = viewClicked.findViewById<ImageView>(R.id.appIcon)
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            activity as Activity, v, ViewCompat.getTransitionName(v))
                    val intent = Intent(context, GroupsAppActivity::class.java)
                    intent.putExtra(GroupsAppActivity.EXTRA_GROUP,
                            adapter.getItem(position) as VisibleGroup)
                    ActivityCompat.startActivity(this, intent, options.toBundle())
                }
            }
        }
        mRecyclerView.adapter = mGroupsAdapter

        mRecyclerView.addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        mGroupsAdapter.setEmptyView(R.layout.layout_empty, mRecyclerView)
        refresh.isEnabled = false
        return rootView
    }

    override fun onResume() {
        super.onResume()
        mPresenter.apply {
            takeView(this@GroupsFragment)
            context?.run { loadGroups(this, false) }
        }
    }

    override fun onDestroyView() {
        mPresenter.dropView()
        mUnBinder.unbind()
        super.onDestroyView()
    }

    override fun showLoadingView() {
        mLoadingView.visibility = View.VISIBLE
        mFunnyLoader.start()
        mRecyclerView.visibility = View.GONE
    }

    override fun hideLoadingView() {
        mLoadingView.visibility = View.GONE
        mFunnyLoader.stop()
        mRecyclerView.visibility = View.VISIBLE
    }

    override fun showGroups(groups: List<VisibleGroup>) {
        mGroupsAdapter.replaceData(groups)
    }

    override fun isAlive(): Boolean = isAdded
}
