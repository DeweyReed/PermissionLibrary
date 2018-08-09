package com.finalweek10.permission.di

import com.finalweek10.permission.ui.detail.AppDetailActivity
import com.finalweek10.permission.ui.detail.AppDetailModule
import com.finalweek10.permission.ui.groupsapp.GroupsAppActivity
import com.finalweek10.permission.ui.groupsapp.GroupsAppModule
import com.finalweek10.permission.ui.main.MainActivity
import com.finalweek10.permission.ui.main.MainModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = [(MainModule::class)])
    internal abstract fun mainActivity(): MainActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [(GroupsAppModule::class)])
    internal abstract fun groupAppsActivity(): GroupsAppActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [(AppDetailModule::class)])
    internal abstract fun appDetailActivity(): AppDetailActivity
}
