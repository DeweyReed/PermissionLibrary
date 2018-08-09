package com.finalweek10.permission.extension

import java.util.*

/**
 * Created on 2017/9/14.
 */

// MY_CRAZY_IDEA => myCrazyIdea
fun String.toLowerCamelCase(): String {
    val token = StringTokenizer(this, "_")
    return if (token.hasMoreTokens()) {
        val str = StringBuilder(token.nextToken().toLowerCase())
        while (token.hasMoreTokens()) {
            val s = token.nextToken()
            str.append(Character.toUpperCase(s[0])).append(s.substring(1).toLowerCase())
        }
        str.toString()
    } else {
        this
    }
}