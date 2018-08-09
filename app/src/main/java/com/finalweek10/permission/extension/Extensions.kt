@file:Suppress("unused")

package com.finalweek10.permission.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

/**
 * Created on 2017/9/16.
 */

fun AppCompatActivity.addFragmentToActivity(frameId: Int,
                                            fragment: Fragment) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.add(frameId, fragment)
    transaction.commit()
}

/**
 * Make sure it has the theme of AppTheme.About
 */
fun Activity.startActivityWithExplode(`class`: Class<*>) {
    if (isLOrLater()) {
        startActivity(Intent(this, `class`),
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    } else {
        startActivity(Intent(this, `class`))
    }
}

@SuppressLint("NewApi")
fun Context.getSharedPreference(): SharedPreferences {
    var storageContext = this
    requiresNOrLater {
        //        val name = PreferenceManager.getDefaultSharedPreferencesName(this)
        storageContext = this.createDeviceProtectedStorageContext()
        // moveSharedPreferencesFrom seems causing SharedPreferences lost on some devices
//        if (!storageContext.moveSharedPreferencesFrom(this, name)) {
//            Log.e(storageContext.getString(R.string.app_name),
//                    "Failed to migrate shared preferences")
//        }
    }
    return PreferenceManager.getDefaultSharedPreferences(storageContext)
}

fun isNOrLater(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

fun requiresNOrLater(f: () -> Unit) {
    if (isNOrLater()) {
        f()
    }
}

fun isLOrLater(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

fun isMOrLater(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

fun requiresLOrLater(f: () -> Unit) {
    if (isLOrLater()) {
        f()
    }
}

fun requiresOOrLater(f: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        f()
    }
}

inline fun requiresBeforeL(f: () -> Unit) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        f()
    }
}

fun Context.openPlayStoreAppPage(): Intent =
        Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))

fun PackageManager.isPackageEnabled(packageName: String): Boolean = try {
    getApplicationInfo(packageName, 0).enabled
} catch (e: PackageManager.NameNotFoundException) {
    true
}
