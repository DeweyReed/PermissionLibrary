package com.finalweek10.permission.data.helper

import org.apache.commons.io.filefilter.AbstractFileFilter
import java.io.File


/**
 * Created on 2017/10/23.
 */

class SpecialFileFilter(file: File) : AbstractFileFilter() {
    private val length = file.path.length

    override fun accept(new: File?): Boolean {
        // If we include these files, the search time will increase from 8s to 30s
        // Fuck tencent!
        if (new == null) return false
        return !(new.path.startsWith("/tencent/MicroMsg", length)
                || new.path.startsWith("/tencent/tassistant/ThumbnailCache", length)
                || new.path.startsWith("/tencent/msflogs", length)
                || new.path.startsWith("/tencent/MobileQQ/diskcache", length)
                || new.name.toLowerCase() == "cache")
    }
}