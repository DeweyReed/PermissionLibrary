package com.finalweek10.permission.receiver

/**
 * Created on 2017/10/6.
 */

//@ReceiverScoped
//class ReceiverPresenter @Inject constructor(
//        private val mPermRepository: PermRepository,
//        private val mPrefHelper: PreferenceHelper) : ReceiverContract.Presenter {
//    //
////    private lateinit var nm: NotificationManager
////    private lateinit var mContext: Context
////
////    private var mNotificationId = 0
////
////    companion object {
////        private val channelId by lazy { "permission_app" }
////    }
////
////    /**
////     * Updating app process ==> PACKAGE_REMOVED, PACKAGE_ADDED
////     * We handle three types of broadcasts
////     * 1: new app is installed. ADDED + REPLACED == false => add one app
////     * 2: one app is upgraded. ADDED + REPLACED == true => remove old one and add new one
////     * 3: one app is uninstalled. PACKAGE_FULLY_REMOVED => remove one
////     *
////     * Therefore, we have two actions. One for adding and the other for removing.
////     */
////    override fun dispatchReceivedAction(context: Context, intent: Intent) {
////        nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
////        mContext = context
////
////        setUpNotificationChannel()
////
////        var packageName = intent.data.toString()
////        if (packageName.startsWith("package:")) {
////            packageName = packageName.removePrefix("package:").trim()
////        }
////
////        var showNotification = false
////        var observable: Observable<Application>? = null
////        when (intent.action) {
////            Intent.ACTION_PACKAGE_FULLY_REMOVED -> observable = removeOneApp(packageName)
////            Intent.ACTION_PACKAGE_ADDED -> {
////                observable = if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
////                    removeOneApp(packageName).concatWith(addOneApp(packageName))
////                } else {
////                    showNotification = true
////                    addOneApp(packageName)
////                }
////            }
////        }
////        observable?.apply {
////            subscribeOn(Schedulers.io())
////                    .observeOn(AndroidSchedulers.mainThread())
////                    .subscribe { (_id, pn, label, versionCode, versionName, isSystem, more) ->
//////                        val count = more.toIntOrNull()
//////                        if (_id != PermRepository.NO_ID && count != null && showNotification
//////                                && mPrefHelper.isShowingNotification) {
//////                            showAddedAppNotification(VisibleApp(_id, pn,
//////                                    label, versionCode, versionName, isSystem, more,
//////                                    context.getApplicationIcon(pn), count))
//////                        }
////                    }
////        }
////    }
////
////    private fun addOneApp(packageName: String)
////            = Observable.fromCallable(mPermRepository.addOneApp(mContext, packageName))
////
////    private fun removeOneApp(packageName: String)
////            = Observable.fromCallable(mPermRepository.removeOneApp(packageName))
////
////    @SuppressLint("NewApi")
////    private fun setUpNotificationChannel() {
////        requiresOOrLater {
////            if (nm.getNotificationChannel(channelId) == null) {
////                nm.createNotificationChannel(NotificationChannel(
////                        channelId, mContext.getString(R.string.notify_channel_name),
////                        NotificationManager.IMPORTANCE_DEFAULT).apply {
////                    enableLights(false)
////                    enableVibration(false)
////                    setShowBadge(true)
////                    setBypassDnd(false)
////                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
////                })
////            }
////        }
////    }
////
////    private fun showAddedAppNotification(app: VisibleApp) {
////        ++mNotificationId
////        nm.notify(mNotificationId, NotificationCompat.Builder(mContext, channelId)
////                .setLocalOnly(true)
////                .setAutoCancel(true)
////                .setCategory(NotificationCompat.CATEGORY_REMINDER)
////                .setSmallIcon(R.mipmap.ic_launcher_foreground)
////                .setContentTitle(mContext.getString(R.string.notify_title, app.label))
////                .setContentText(mContext.getString(R.string.notify_content))
////                .setContentIntent(PendingIntent.getActivity(mContext, 0,
////                        Intent(mContext, AppDetailActivity::class.java)
////                                .putExtra(AppDetailActivity.EXTRA_APP, app)
////                                .putExtra(AppDetailActivity.EXTRA_BACK_STACK, false)
////                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
////                                        or Intent.FLAG_ACTIVITY_SINGLE_TOP),
////                        PendingIntent.FLAG_ONE_SHOT))
////                .build())
////    }
////
//    override fun takeView(view: ReceiverContract.View) {}
//
//    override fun dropView() {}
//}