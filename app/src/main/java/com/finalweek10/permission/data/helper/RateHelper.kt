package com.finalweek10.permission.data.helper

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.finalweek10.permission.R
import com.finalweek10.permission.extension.openPlayStoreAppPage

/**
 * Created on 2017/9/27.
 */

class RateHelper(private val mPrefHelper: PreferenceHelper) {
    fun rateAdvice(context: Context) {
        mPrefHelper.launchTimes += 1
        if (!mPrefHelper.neverRate && mPrefHelper.launchTimes >= mPrefHelper.launchTimeLimit) {
            MaterialDialog.Builder(context)
                    .cancelable(false)
                    .title(R.string.rate_title)
                    .content(R.string.rate_content)
                    .positiveText(android.R.string.ok)
                    .onPositive { _, _ ->
                        mPrefHelper.neverRate = true
                        val intent = context.openPlayStoreAppPage()
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        }
                    }
                    .neutralText(R.string.rate_later)
                    .onNeutral { _, _ -> mPrefHelper.launchTimeLimit += 2 }
                    .negativeText(R.string.rate_no)
                    .onNegative { _, _ -> mPrefHelper.neverRate = true }
                    .show()
        }
    }
}