package com.finalweek10.permission.receiver

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.finalweek10.permission.di.ReceiverScoped
import dagger.android.DaggerBroadcastReceiver

@ReceiverScoped
class AppReceiver : DaggerBroadcastReceiver(), ReceiverContract.View {

//    @Inject
//    lateinit var mPresenter: ReceiverContract.Presenter

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
//        mPresenter.apply {
//            dispatchReceivedAction(context, intent)
//        }
    }
}
