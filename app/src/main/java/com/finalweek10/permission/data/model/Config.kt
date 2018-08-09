@file:Suppress("MemberVisibilityCanBePrivate")

package com.finalweek10.permission.data.model

/**
 * Created on 2017/10/13.
 */

class ShowConfig private constructor(
        val normal: Boolean,
        val system: Boolean,
        val disabled: Boolean) {

    fun toIndices(): Array<Int> {
        val list = arrayListOf<Int>()
        if (normal) list.add(0)
        if (system) list.add(1)
        if (disabled) list.add(2)
        return list.toTypedArray()
    }

    @Suppress("MemberVisibilityCanPrivate")
    class Builder {
        var normal: Boolean? = null
            private set
        var system: Boolean? = null
            private set
        var disabled: Boolean? = null
            private set

        fun normal(b: Boolean = true) = apply { normal = b }
        fun system(b: Boolean = true) = apply { system = b }
        fun disabled(b: Boolean = true) = apply { disabled = b }
        fun indices(a: Array<Int>) = apply {
            a.forEach {
                when (it) {
                    0 -> normal = true
                    1 -> system = true
                    2 -> disabled = true
                }
            }
        }

        fun build() = ShowConfig(
                normal == true, system == true, disabled == true)
    }
}