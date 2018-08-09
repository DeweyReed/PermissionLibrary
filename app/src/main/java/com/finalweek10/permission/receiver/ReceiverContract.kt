package com.finalweek10.permission.receiver

import com.finalweek10.permission.ui.BasePresenter
import com.finalweek10.permission.ui.BaseView

/**
 * Created on 2017/10/6.
 */

interface ReceiverContract {
    interface View : BaseView

    interface Presenter : BasePresenter<View> {
//        fun dispatchReceivedAction(context: Context, intent: Intent)
    }
}