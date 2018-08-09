package com.finalweek10.permission.extension

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.support.annotation.*
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.TypedValue
import com.finalweek10.permission.R
import com.finalweek10.permission.data.db.DatabaseUtil


/**
 * Created on 2017/8/11.
 */

// Android Resource Naming Cheat Sheet
// Layout: what_where.xml(activity_main.xml)
// String: where_description(all_done)
// Drawable: where_description_size(main_background)
// ID: what_where_description(linearlayout_main_fragmentcontainer)
// Dimen: what_where_description_size(keyline_all_text)

@AnyRes
fun Context.findResourceId(name: String, type: String, @AnyRes default: Int): Int {
    val id = resources.getIdentifier(name, type, packageName)
    return if (id == 0) default else id
}

fun Context.drawable(@DrawableRes res: Int): Drawable = ContextCompat.getDrawable(this, res)
        ?: ContextCompat.getDrawable(this, R.drawable.ic_error)!!

@ColorInt
fun Context.color(@ColorRes res: Int): Int = ContextCompat.getColor(this, res)

@ColorInt
fun Context.themeColor(@AttrRes res: Int): Int {
    val value = TypedValue()
    theme.resolveAttribute(res, value, true)
    return value.data
}

fun Context.simpleGroupNameToIcon(name: String): Drawable = ContextCompat.getDrawable(this,
        findResourceId("ic_${name.toLowerCase()}", "drawable",
                R.drawable.ic_error))!!

fun Context.simpleGroupNameToDescription(name: String): String = getString(findResourceId("group_desp_${name.toLowerCase()}", "string",
        R.string.group_desp_other))


fun Context.permissionToLabel(perm: String): String = permissionStringResource("permlab_", perm.calculateContent())

fun Context.permissionToDescription(perm: String): String = permissionStringResource("permdesc_", perm.calculateContent())

private fun String.calculateContent(): String = if (isNotEmpty() && get(0).isLowerCase()) this else toLowerCamelCase()

private fun Context.permissionStringResource(head: String, content: String): String = getString(findResourceId("$head$content", "string", R.string.empty_string))

fun String.simplePermNameToUsesPermission() = if (contains("VOICEMAIL")) DatabaseUtil.voicemailPermissionPrefix + this
else DatabaseUtil.androidPermissionPrefix + this

fun Context.getApplicationIcon(packageName: String): Drawable = applicationContext.packageManager
        .getApplicationIconWithoutException(packageName, drawable(R.drawable.ic_error))

fun PackageManager.getApplicationIconWithoutException(
        packageName: String, default: Drawable): Drawable = try {
    this.getApplicationIcon(packageName) ?: default
} catch (e: Exception) {
    default
}

@Suppress("PropertyName")
val APK_INTENT_TYPE by lazy { "application/vnd.android.package-archive" }


///**
// * @param permission from database format
// * @return specific permission description with a specific package name
// */
//fun Context.getSpecificPermissionDescription(
//        packageName: String, permission: String): String
//        = getString(findResourceId("${packageName}_${permission.toLowerCamelCase()}",
//        "string", R.string.empty_string))

fun String.getFirstChar(): String = if (TextUtils.isEmpty(this)) "" else this.substring(0, 1)