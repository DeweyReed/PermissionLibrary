package com.finalweek10.permission.extension

import com.finalweek10.permission.data.model.VisibleApp
import com.github.stuxuhai.jpinyin.PinyinHelper

/**
 * Created on 2017/9/28.
 */

fun List<VisibleApp>.sort(usePinYin: Boolean) =
        if (usePinYin) {
            sortedBy { PinyinHelper.getShortPinyin(it.label.getFirstChar()).toUpperCase() }
        } else {
            sortedBy { it.label.getFirstChar().toUpperCase() }
        }

fun VisibleApp.sectionName(usePinYin: Boolean) =
        if (usePinYin) {
            PinyinHelper.getShortPinyin(label.getFirstChar()).toUpperCase()
        } else {
            label.getFirstChar().toUpperCase()
        }