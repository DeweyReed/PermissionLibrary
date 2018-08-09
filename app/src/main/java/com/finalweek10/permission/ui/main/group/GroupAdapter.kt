package com.finalweek10.permission.ui.main.group

import android.support.v4.view.ViewCompat
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.finalweek10.permission.R
import com.finalweek10.permission.data.model.VisibleGroup

/**
 * Created on 2017/9/24.
 */

class GroupAdapter : BaseQuickAdapter<VisibleGroup, BaseViewHolder>(R.layout.item_main, null) {
    override fun convert(helper: BaseViewHolder?, item: VisibleGroup?) {
        if (helper != null && item != null) {
            val iconView = helper.getView<ImageView>(R.id.appIcon)
            iconView.setImageDrawable(item.icon)
            ViewCompat.setTransitionName(iconView, "transition_group_toolbar")
            helper.setText(R.id.itemName, item.descriptionName)
                    .setText(R.id.itemDetail, helper.itemView.context.resources.getQuantityString(
                            R.plurals.list_item_app_count, item.appCount, item.appCount))
        }
    }
}