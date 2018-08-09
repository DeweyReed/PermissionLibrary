package com.finalweek10.permission.ui.detail

import com.finalweek10.permission.data.model.ApkFile
import com.finalweek10.permission.data.model.VisibleApp
import com.finalweek10.permission.di.ActivityScoped
import dagger.Binds
import dagger.Module
import dagger.Provides
import java.util.concurrent.atomic.AtomicBoolean

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * [AppDetailPresenter].
 */
@Suppress("unused")
@Module
abstract class AppDetailModule {
    @ActivityScoped
    @Binds
    internal abstract fun appDetailPresenter(presenter: AppDetailPresenter):
            AppDetailContract.Presenter

    @Module
    companion object {
        @JvmStatic
        @Provides
        @ActivityScoped
        internal fun provideApp(activity: AppDetailActivity): VisibleApp {
            val result: VisibleApp? = if (activity.intent.hasExtra(AppDetailActivity.EXTRA_APP))
                activity.intent.getParcelableExtra(AppDetailActivity.EXTRA_APP)
            else null
            return result ?: VisibleApp(-1, "", "",
                    0, "", false, "",
                    null, 0)
        }

        @JvmStatic
        @Provides
        @ActivityScoped
        internal fun provideHasBackStack(activity: AppDetailActivity): AtomicBoolean =
                AtomicBoolean(activity.intent.getBooleanExtra(
                        AppDetailActivity.EXTRA_BACK_STACK, true))

        @JvmStatic
        @Provides
        @ActivityScoped
        internal fun provideApkFile(activity: AppDetailActivity): ApkFile {
            val intent = activity.intent
            return if (intent.hasExtra(AppDetailActivity.EXTRA_APK_FILE)) {
                intent.getParcelableExtra(AppDetailActivity.EXTRA_APK_FILE)
            } else {
                ApkFile.emptyFile
            }
        }
    }
}