package com.finalweek10.permission.ui.main

import com.finalweek10.permission.di.ActivityScoped
import com.finalweek10.permission.di.FragmentScoped
import com.finalweek10.permission.ui.main.apk.ApkFragment
import com.finalweek10.permission.ui.main.apk.ApkPresenter
import com.finalweek10.permission.ui.main.app.AppsFragment
import com.finalweek10.permission.ui.main.app.AppsPresenter
import com.finalweek10.permission.ui.main.group.GroupsFragment
import com.finalweek10.permission.ui.main.group.GroupsPresenter
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class MainModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun appsFragment(): AppsFragment

    @ActivityScoped
    @Binds
    abstract fun appsPresenter(presenter: AppsPresenter): MainContract.AppsPresenter

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun groupsFragment(): GroupsFragment

    @ActivityScoped
    @Binds
    abstract fun groupsPresenter(presenter: GroupsPresenter): MainContract.GroupsPresenter

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun apkFragment(): ApkFragment

    @ActivityScoped
    @Binds
    abstract fun apkPresenter(presenter: ApkPresenter): MainContract.ApkPresenter
}
