package com.finalweek10.permission

import android.os.StrictMode
import android.support.v7.app.AppCompatDelegate
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.finalweek10.permission.data.helper.PreferenceHelper
import com.finalweek10.permission.di.DaggerAppComponent
import com.finalweek10.permission.extension.requiresBeforeL
import com.finalweek10.permission.ui.about.CrashActivity
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import org.jetbrains.anko.longToast
import javax.inject.Inject

/**
 * Created on 2017/9/12.
 */

@Suppress("MemberVisibilityCanPrivate")
class App : DaggerApplication() {

    init {
        requiresBeforeL {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    @Inject
    lateinit var mPrefHelper: PreferenceHelper

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return
            }
            LeakCanary.install(this)
            // Normal app init code...
//            Stetho.initializeWithDefaults(this)
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .permitDiskReads()
                    .permitDiskWrites()
                    .penaltyLog()
//                    .penaltyDeath()
                    .penaltyDialog()
                    .build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
//                    .penaltyDeath()
                    .build())
        }

        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_CRASH)
                .errorActivity(CrashActivity::class.java)
                .apply()

        mPrefHelper.run {
            if (isFirstLaunch) {
                isFirstLaunch = false
                shouldShowUpdateMessage = false
                usePinYin = Runtime.getRuntime()?.maxMemory() ?: 0L > 16777216L
                longToast(R.string.first_time_launch_text)
            }
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component = DaggerAppComponent.builder().application(this).build()
        component.inject(this)
        return component
    }
}
