package com.finalweek10.permission.ui.main.apk

import android.support.v4.view.ViewCompat
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.finalweek10.permission.R
import com.finalweek10.permission.data.model.ApkFile
import com.finalweek10.permission.data.model.VisibleApp

/**
 * Created on 2017/9/24.
 */

class ApkAdapter
    : BaseQuickAdapter<Pair<ApkFile, VisibleApp>, BaseViewHolder>(R.layout.item_main, null) {
    override fun convert(helper: BaseViewHolder?, item: Pair<ApkFile, VisibleApp>?) {
        if (helper != null && item != null) {
            val context = helper.itemView.context
            val apk = item.first
            helper.setText(R.id.itemName, apk.name)
                    .setText(R.id.itemDetail, context.resources.getQuantityString(
                            R.plurals.list_item_perm_count,
                            apk.permCount, apk.permCount))
            val iconView = helper.getView<ImageView>(R.id.appIcon)
            iconView.setImageDrawable(apk.icon)
            ViewCompat.setTransitionName(iconView, "transition_icon")
        }
    }
}