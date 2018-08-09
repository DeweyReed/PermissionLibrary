package com.finalweek10.permission.di

import android.app.Application
import com.finalweek10.permission.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [(ApplicationModule::class),
    (ActivityBindingModule::class),
    (AndroidSupportInjectionModule::class)])
interface AppComponent : AndroidInjector<DaggerApplication> {

    fun inject(instance: App)

    override fun inject(instance: DaggerApplication)

    @Suppress("unused")
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): AppComponent.Builder

        fun build(): AppComponent
    }
}
