package com.finalweek10.permission.ui.main.group

import android.content.Context
import com.finalweek10.permission.data.model.VisibleGroup
import com.finalweek10.permission.data.source.PermRepository
import com.finalweek10.permission.di.ActivityScoped
import com.finalweek10.permission.extension.simpleGroupNameToDescription
import com.finalweek10.permission.extension.simpleGroupNameToIcon
import com.finalweek10.permission.ui.main.MainContract
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created on 2017/9/14.
 */
@ActivityScoped
class GroupsPresenter @Inject constructor(
        private val mPermRepository: PermRepository) : MainContract.GroupsPresenter {

    private var mView: MainContract.GroupsView? = null

    private var mFirstLoad = true

    private var mDisposable: Disposable? = null

    override fun takeView(view: MainContract.GroupsView) {
        mView = view
    }

    override fun dropView() {
        mView = null
        mDisposable?.dispose()
        mFirstLoad = true
    }

    override fun loadGroups(context: Context, force: Boolean) {
        if (force || mFirstLoad) {
//            Log.i("Final", "begin loading groups")
            mView?.showLoadingView()
            mDisposable?.dispose()
            mDisposable = Observable.fromCallable(
                    mPermRepository.getAllGroupsAndTheirApplicationCount())
                    .map {
                        val groups = arrayListOf<VisibleGroup>()
                        it.forEach { (group, count) ->
                            groups.add(VisibleGroup(group._id, group.name,
                                    context.simpleGroupNameToDescription(group.name),
                                    context.simpleGroupNameToIcon(group.name),
                                    count))
                        }
                        groups
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        mView?.run {
                            showGroups(it)
                            hideLoadingView()
                        }
                    }
        }
        mFirstLoad = false
    }
}