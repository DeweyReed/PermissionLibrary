package com.finalweek10.permission.ui.detail

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.finalweek10.permission.R
import com.finalweek10.permission.data.model.PERM_OFF
import com.finalweek10.permission.data.model.PERM_ON
import com.finalweek10.permission.data.model.SectionPerm
import com.finalweek10.permission.data.model.VisiblePerm
import com.finalweek10.permission.extension.color

/**
 * Created on 2017/9/24.
 */

class AppDetailAdapter : BaseSectionQuickAdapter<SectionPerm, BaseViewHolder>(
        R.layout.item_app_perm, R.layout.item_header, null) {

    var showState = true

    var showDesp = true
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun convert(helper: BaseViewHolder?, item: SectionPerm?) {
        if (helper != null && item != null) {
            val context = helper.itemView.context
            val perm = item.t as VisiblePerm

            helper.addOnLongClickListener(R.id.layoutItemAppPerm)

            val labelView = helper.getView<TextView>(R.id.itemLabel)
            val permLabel = perm.permLabel
            if (!TextUtils.isEmpty(permLabel)) {
                labelView.text = permLabel
                labelView.visibility = View.VISIBLE
            } else {
                labelView.visibility = View.GONE
            }

            val despView = helper.getView<TextView>(R.id.itemDesp)
            val desp = perm.permDescription
            if (showDesp && !TextUtils.isEmpty(desp)) {
                despView.text = desp
                despView.visibility = View.VISIBLE
            } else {
                despView.visibility = View.GONE
            }

            val nameView = helper.getView<TextView>(R.id.itemSimpleName)
            if (showDesp || TextUtils.isEmpty(permLabel)) {
                nameView.text = perm.simpleName
                nameView.visibility = View.VISIBLE
            } else {
                nameView.visibility = View.GONE
            }

            val stateView = helper.getView<TextView>(R.id.itemState)
            if (!showState) {
                stateView.visibility = View.GONE
                return
            }
            helper.addOnClickListener(R.id.itemState)
            when (perm.state) {
                PERM_ON -> stateView.run {
                    visibility = View.VISIBLE
                    text = context.getString(R.string.permission_state_on)
                    setTextColor(context.color(R.color.alert_red))
                }
                PERM_OFF -> stateView.run {
                    visibility = View.VISIBLE
                    text = context.getString(R.string.permission_state_off)
                    setTextColor(context.color(R.color.colorPrimaryDark))
                }
                else -> stateView.visibility = View.GONE
            }
        }
    }

    override fun convertHead(helper: BaseViewHolder?, item: SectionPerm?) {
        if (helper != null && item != null) {
            helper.addOnClickListener(R.id.itemHeaderText)
            helper.getView<TextView>(R.id.itemHeaderText).apply {
                text = if (item.count > 1) "${item.header}(${item.count})" else item.header
                setTextColor(mContext.color(if (item.dangerous) {
                    isClickable = true
                    R.color.alert_red
                } else {
                    isClickable = false
                    R.color.colorAccent
                }))
            }
        }
    }
}