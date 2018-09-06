package com.finalweek10.permission.data.helper

import android.content.Context
import android.content.SharedPreferences
import com.finalweek10.permission.data.model.ShowConfig
import com.finalweek10.permission.extension.getSharedPreference
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created on 2017/9/18.
 */

@Singleton
class PreferenceHelper @Inject constructor(context: Context) {

    private var prefs: SharedPreferences = context.getSharedPreference()

    companion object {
        private const val FIRST_LAUNCH = "first_launch"

        private const val SHOW_NORMAL = "show_normal"
        private const val SHOW_SYSTEM = "show_system"
        private const val SHOW_DISABLED = "show_disabled"

        //        const private val SHOW_NOTIFICATION = "show_notification"
        private const val SHOW_PERM_DESP = "show_perm_desp"

        private const val APK_IS_DELETED = "apk_is_deleted"

        private const val LAUNCH_TIMES = "launch_times"
        private const val LAUNCH_TIME_LIMIT = "launch_time_limit"
        private const val NEVER_RATE = "never_rate"

        private const val USE_PINYIN = "use_pin_yin"

        private const val UPDATE_OLD = "update_1_38"
        private const val UPDATE_NEW = "update_1_4_0"

        private fun SharedPreferences.setBoolean(name: String, value: Boolean) {
            edit().putBoolean(name, value).apply()
        }

        private fun SharedPreferences.setInt(name: String, value: Int) {
            edit().putInt(name, value).apply()
        }
    }

    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(FIRST_LAUNCH, true)
        set(value) = prefs.setBoolean(FIRST_LAUNCH, value)

    var showConfig: ShowConfig
        get() = ShowConfig.Builder()
                .normal(prefs.getBoolean(SHOW_NORMAL, true))
                .system(prefs.getBoolean(SHOW_SYSTEM, false))
                .disabled(prefs.getBoolean(SHOW_DISABLED, false)).build()
        set(value) = prefs.edit()
                .putBoolean(SHOW_NORMAL, value.normal)
                .putBoolean(SHOW_SYSTEM, value.system)
                .putBoolean(SHOW_DISABLED, value.disabled).apply()

//    var isShowingNotification: Boolean
//        get() = prefs.getBoolean(SHOW_NOTIFICATION, true)
//        set(value) = prefs.setBoolean(SHOW_NOTIFICATION, value)

    var isShowingPermDesp: Boolean
        get() = prefs.getBoolean(SHOW_PERM_DESP, true)
        set(value) = prefs.setBoolean(SHOW_PERM_DESP, value)

    var isApkDeleted: Boolean
        get() = prefs.getBoolean(APK_IS_DELETED, false)
        set(value) = prefs.setBoolean(APK_IS_DELETED, value)

    var usePinYin: Boolean
        get() = prefs.getBoolean(USE_PINYIN, false)
        set(value) = prefs.setBoolean(USE_PINYIN, value)

    var shouldShowUpdateMessage: Boolean
        get() = prefs.getBoolean(UPDATE_NEW, true)
        set(value) = prefs.setBoolean(UPDATE_NEW, value)

    fun removeOldUpdateSP() {
        prefs.edit().remove(UPDATE_OLD).apply()
    }

    //
    // rate
    //

    var launchTimes: Int
        get() = prefs.getInt(LAUNCH_TIMES, 0)
        set(value) = prefs.setInt(LAUNCH_TIMES, value)

    var launchTimeLimit: Int
        get() = prefs.getInt(LAUNCH_TIME_LIMIT, 3)
        set(value) = prefs.setInt(LAUNCH_TIME_LIMIT, value)

    var neverRate: Boolean
        get() = prefs.getBoolean(NEVER_RATE, false)
        set(value) = prefs.setBoolean(NEVER_RATE, value)
}