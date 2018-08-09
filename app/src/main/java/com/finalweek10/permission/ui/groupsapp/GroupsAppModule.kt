package com.finalweek10.permission.ui.groupsapp

import com.finalweek10.permission.data.model.VisibleGroup
import com.finalweek10.permission.di.ActivityScoped
import com.finalweek10.permission.di.FragmentScoped
import com.finalweek10.permission.ui.main.MainContract
import com.finalweek10.permission.ui.main.app.AppsPresenter
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class GroupsAppModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun groupsAppFragment(): GroupsAppFragment

    @ActivityScoped
    @Binds
    abstract fun groupsAppPresenter(presenter: AppsPresenter)
            : MainContract.AppsPresenter

    @Module
    companion object {
        @JvmStatic
        @Provides
        @ActivityScoped
        internal fun provideGroup(activity: GroupsAppActivity): VisibleGroup =
                activity.intent?.getParcelableExtra(GroupsAppActivity.EXTRA_GROUP)
                        ?: VisibleGroup(0, "", "",
                                null, 0)
    }
}