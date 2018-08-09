package com.finalweek10.permission.di

import android.app.Application
import android.content.Context
import com.finalweek10.permission.data.db.PermissionDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Suppress("unused")
@Module
internal class ApplicationModule {
    @Provides
    fun provideContext(application: Application): Context = application

    @Provides
    @Singleton
    fun provideDatabase(application: Application): PermissionDatabase =
            PermissionDatabase.createPersistentDatabase(application)
}

