@file:Suppress("UNUSED_PARAMETER")

package com.finalweek10.permission.extension

import com.finalweek10.permission.data.model.VisibleApp

/**
 * Created on 2017/9/28.
 */

fun List<VisibleApp>.sort(usePinYin: Boolean) =
        sortedBy { it.label.getFirstChar().toUpperCase() }

fun VisibleApp.sectionName(usePinYin: Boolean) = label.getFirstChar().toUpperCase()