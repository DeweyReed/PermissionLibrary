package com.finalweek10.permission.data.helper

import android.app.Activity
import com.afollestad.materialdialogs.MaterialDialog
import com.finalweek10.permission.R

/**
 * Created on 2017/10/5.
 */

class UpdateHelper(private val mPrefHelper: PreferenceHelper) {
    fun update(context: Activity) {
        mPrefHelper.apply {
            if (shouldShowUpdateMessage) {
                shouldShowUpdateMessage = false
                removeOldUpdateSP()
                MaterialDialog.Builder(context)
                        .autoDismiss(false)
                        .cancelable(true)
                        .title(R.string.update_title)
                        .items(R.array.update_items)
                        .positiveText(android.R.string.ok)
                        .onPositive { dialog, _ -> dialog.dismiss() }
                        .show()
            }
        }
    }
}