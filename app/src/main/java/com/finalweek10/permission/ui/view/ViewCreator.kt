package com.finalweek10.permission.ui.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.finalweek10.permission.R
import com.finalweek10.permission.data.model.VisiblePerm
import com.finalweek10.permission.extension.requiresLOrLater
import com.finalweek10.permission.extension.themeColor
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.github.javiersantos.materialstyleddialogs.enums.Style
import com.google.android.flexbox.FlexboxLayout
import org.jetbrains.anko.dip
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast

/**
 * Created on 2017/9/25.
 */

@SuppressLint("NewApi")
fun Context.createCuteLabel(content: String): View {
    val mRoundText = RoundTextView(this)
    val layoutParamsText = FlexboxLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)

    layoutParamsText.setMargins(0, dip(4), dip(4), dip(4))

    mRoundText.apply {
        requiresLOrLater {
            elevation = dip(4).toFloat()
        }
        setPadding(dip(4), 0, dip(4), dip(2))
        setTextColor(Color.WHITE)
        text = content

        maxLines = 1

        setBgColor(themeColor(R.attr.colorAccent))
        setCorner(dip(2))

        layoutParams = layoutParamsText
    }

    return mRoundText
}

fun Activity.createLabelExplanationDialog(title: String, explanation: String) {
    MaterialStyledDialog.Builder(this)
            .setTitle(title)
            .setDescription(explanation)
            .setHeaderColor(R.color.colorPrimaryDark)
            .setStyle(Style.HEADER_WITH_TITLE)
            .setPositiveText(android.R.string.ok)
            .show()
}

fun Activity.createPermissionItemItemActionDialog(data: VisiblePerm) {
    MaterialDialog.Builder(this)
            .items(R.array.perm_item_copy_options)
            .itemsCallback { _, _, position, _ ->
                val content = when (position) {
                    0 -> data.permLabel
                    1 -> data.simpleName
                    2 -> data.permDescription
                    3 -> arrayOf(data.permLabel, data.simpleName, data.permDescription)
                            .joinToString(separator = "\n")
                    else -> {
                        copyToClipboard(data.simpleName)
                        longToast(R.string.toast_check_in_doc)
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/aosp-mirror/platform_frameworks_base/blob/master/core/res/AndroidManifest.xml")))
                        return@itemsCallback
                    }
                }
                copyToClipboard(content)
                toast(content)
            }
            .show()
}

fun Context.copyToClipboard(text: String) {
    (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
            .primaryClip = ClipData.newPlainText("text", text)
}

fun Activity.createHelpDialog() {
    MaterialDialog.Builder(this)
            .autoDismiss(false)
            .title(R.string.help_title)
            .items(R.array.help_items)
            .itemsCallback { _, _, position, _ ->
                val title: Int
                val content: Int
                when (position) {
                    0 -> {
                        title = R.string.label_system
                        content = R.string.label_system_explanation
                    }
                    1 -> {
                        title = R.string.label_dangerous_permission
                        content = R.string.label_dangerous_permission_explanation
                    }
                    2 -> {
                        title = R.string.label_change_permissions
                        content = R.string.help_item_change_permissions_content
                    }
                    else -> {
                        title = R.string.label_lost_perm
                        content = R.string.help_item_lost_perm_content
                    }
                }
                createLabelExplanationDialog(getString(title), getString(content))
            }
            .positiveText(android.R.string.ok)
            .onPositive { dialog, _ -> dialog.dismiss() }
            .show()
}

fun Context.createDeletionAssureDialog(callback: MaterialDialog.SingleButtonCallback) {
    MaterialDialog.Builder(this)
            .title(R.string.action_title_delete_apk)
            .content(R.string.delete_apk_assure)
            .positiveText(android.R.string.ok)
            .onPositive(callback)
            .negativeText(android.R.string.cancel)
            .show()
}