package com.finalweek10.permission.ui.main.app

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.view.ViewCompat
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.finalweek10.permission.R
import com.finalweek10.permission.data.model.VisibleApp
import com.finalweek10.permission.extension.drawable
import com.finalweek10.permission.extension.getApplicationIconWithoutException
import com.finalweek10.permission.extension.sectionName
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

/**
 * Created on 2017/9/24.
 */

class AppAdapter(mContext: Context, private val usePinYinSort: Boolean) :
        BaseQuickAdapter<VisibleApp, BaseViewHolder>(R.layout.item_main, null),
        FastScrollRecyclerView.SectionedAdapter {

    private val pm: PackageManager = mContext.packageManager

    override fun convert(helper: BaseViewHolder?, item: VisibleApp?) {
        if (helper != null && item != null) {
            var detailText = mContext.resources.getQuantityString(
                    R.plurals.list_item_perm_count, item.permissionCount, item.permissionCount)
            if (item.isSystem) {
                detailText += ", ${mContext.getString(R.string.label_system)}"
            }
            val iconView = helper.getView<ImageView>(R.id.appIcon)
            iconView.setImageDrawable(item.icon ?: pm.getApplicationIconWithoutException(
                    item.packageName, mContext.drawable(R.drawable.ic_error)))
            ViewCompat.setTransitionName(iconView, "transition_icon")
            helper.setText(R.id.itemName, item.label)
                    .setText(R.id.itemDetail, detailText)
        }
    }

    override fun getSectionName(position: Int): String =
            (getItem(position) as VisibleApp).sectionName(usePinYinSort)
}