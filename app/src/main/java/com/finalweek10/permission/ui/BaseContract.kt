package com.finalweek10.permission.ui

/**
 * Created on 2017/9/14.
 */

interface BaseView

interface BasePresenter<in T> {
    fun takeView(view: T)
    fun dropView()
}