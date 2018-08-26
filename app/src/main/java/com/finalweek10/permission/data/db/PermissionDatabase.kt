package com.finalweek10.permission.data.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.ContentValues
import android.content.Context

/**
 * Created on 2017/9/12.
 */

@Database(entities = [(Application::class), (Permission::class), (PermissionGroup::class), (AppPermRelation::class), (PermGroupRelation::class)],
        version = 1,
        exportSchema = true)
abstract class PermissionDatabase : RoomDatabase() {
    companion object {
        private const val DATABASE_NAME = "permission_db"

        fun createPersistentDatabase(context: Context): PermissionDatabase = Room.databaseBuilder(context.applicationContext,
                PermissionDatabase::class.java, DATABASE_NAME)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        DatabaseUtil.databaseInitialization(db)
                    }
                }).build()
    }

    abstract fun applicationDao(): ApplicationDao
    abstract fun permissionDao(): PermissionDao
    abstract fun groupDao(): GroupDao
    abstract fun appPermDao(): AppPermDao
}

object DatabaseUtil {
    const val androidPermissionPrefix = "android.permission."
    const val voicemailPermissionPrefix = "com.android.voicemail.permission."

    // size: 147
    private val permissionsList by lazy {
        arrayOf("ACCESS_CHECKIN_PROPERTIES",
                "ACCESS_COARSE_LOCATION",
                "ACCESS_FINE_LOCATION",
                "ACCESS_LOCATION_EXTRA_COMMANDS",
                "ACCESS_NETWORK_STATE",
                "ACCESS_NOTIFICATION_POLICY",
                "ACCESS_WIFI_STATE",
                "ACCOUNT_MANAGER",
                "ADD_VOICEMAIL",
                "ANSWER_PHONE_CALLS",
                "BATTERY_STATS",
                "BIND_ACCESSIBILITY_SERVICE",
                "BIND_APPWIDGET",
                "BIND_AUTOFILL_SERVICE",
                "BIND_CARRIER_MESSAGING_SERVICE",
                "BIND_CARRIER_SERVICES",
                "BIND_CHOOSER_TARGET_SERVICE",
                "BIND_CONDITION_PROVIDER_SERVICE",
                "BIND_DEVICE_ADMIN",
                "BIND_DREAM_SERVICE",
                "BIND_INCALL_SERVICE",
                "BIND_INPUT_METHOD",
                "BIND_MIDI_DEVICE_SERVICE",
                "BIND_NFC_SERVICE",
                "BIND_NOTIFICATION_LISTENER_SERVICE",
                "BIND_PRINT_SERVICE",
                "BIND_QUICK_SETTINGS_TILE",
                "BIND_REMOTEVIEWS",
                "BIND_SCREENING_SERVICE",
                "BIND_TELECOM_CONNECTION_SERVICE",
                "BIND_TEXT_SERVICE",
                "BIND_TV_INPUT",
                "BIND_VISUAL_VOICEMAIL_SERVICE",
                "BIND_VOICE_INTERACTION",
                "BIND_VPN_SERVICE",
                "BIND_VR_LISTENER_SERVICE",
                "BIND_WALLPAPER",
                "BLUETOOTH",
                "BLUETOOTH_ADMIN",
                "BLUETOOTH_PRIVILEGED",
                "BODY_SENSORS",
                "BROADCAST_PACKAGE_REMOVED",
                "BROADCAST_SMS",
                "BROADCAST_STICKY",
                "BROADCAST_WAP_PUSH",
                "CALL_PHONE",
                "CALL_PRIVILEGED",
                "CAMERA",
                "CAPTURE_AUDIO_OUTPUT",
                "CAPTURE_SECURE_VIDEO_OUTPUT",
                "CAPTURE_VIDEO_OUTPUT",
                "CHANGE_COMPONENT_ENABLED_STATE",
                "CHANGE_CONFIGURATION",
                "CHANGE_NETWORK_STATE",
                "CHANGE_WIFI_MULTICAST_STATE",
                "CHANGE_WIFI_STATE",
                "CLEAR_APP_CACHE",
                "CONTROL_LOCATION_UPDATES",
                "DELETE_CACHE_FILES",
                "DELETE_PACKAGES",
                "DIAGNOSTIC",
                "DISABLE_KEYGUARD",
                "DUMP",
                "EXPAND_STATUS_BAR",
                "FACTORY_TEST",
                "GET_ACCOUNTS",
                "GET_ACCOUNTS_PRIVILEGED",
                "GET_PACKAGE_SIZE",
                "GET_TASKS",
                "GLOBAL_SEARCH",
                "INSTALL_LOCATION_PROVIDER",
                "INSTALL_PACKAGES",
                "INSTALL_SHORTCUT",
                "INSTANT_APP_FOREGROUND_SERVICE",
                "INTERNET",
                "KILL_BACKGROUND_PROCESSES",
                "LOCATION_HARDWARE",
                "MANAGE_DOCUMENTS",
                "MANAGE_OWN_CALLS",
                "MASTER_CLEAR",
                "MEDIA_CONTENT_CONTROL",
                "MODIFY_AUDIO_SETTINGS",
                "MODIFY_PHONE_STATE",
                "MOUNT_FORMAT_FILESYSTEMS",
                "MOUNT_UNMOUNT_FILESYSTEMS",
                "NFC",
                "PACKAGE_USAGE_STATS",
                "PERSISTENT_ACTIVITY",
                "PROCESS_OUTGOING_CALLS",
                "READ_CALENDAR",
                "READ_CALL_LOG",
                "READ_CONTACTS",
                "READ_EXTERNAL_STORAGE",
                "READ_FRAME_BUFFER",
                "READ_INPUT_STATE",
                "READ_LOGS",
                "READ_PHONE_NUMBERS",
                "READ_PHONE_STATE",
                "READ_SMS",
                "READ_SYNC_SETTINGS",
                "READ_SYNC_STATS",
                "READ_VOICEMAIL",
                "REBOOT",
                "RECEIVE_BOOT_COMPLETED",
                "RECEIVE_MMS",
                "RECEIVE_SMS",
                "RECEIVE_WAP_PUSH",
                "RECORD_AUDIO",
                "REORDER_TASKS",
                "REQUEST_COMPANION_RUN_IN_BACKGROUND",
                "REQUEST_COMPANION_USE_DATA_IN_BACKGROUND",
                "REQUEST_DELETE_PACKAGES",
                "REQUEST_IGNORE_BATTERY_OPTIMIZATIONS",
                "REQUEST_INSTALL_PACKAGES",
                "RESTART_PACKAGES",
                "SEND_RESPOND_VIA_MESSAGE",
                "SEND_SMS",
                "SET_ALARM",
                "SET_ALWAYS_FINISH",
                "SET_ANIMATION_SCALE",
                "SET_DEBUG_APP",
                "SET_PREFERRED_APPLICATIONS",
                "SET_PROCESS_LIMIT",
                "SET_TIME",
                "SET_TIME_ZONE",
                "SET_WALLPAPER",
                "SET_WALLPAPER_HINTS",
                "SIGNAL_PERSISTENT_PROCESSES",
                "STATUS_BAR",
                "SYSTEM_ALERT_WINDOW",
                "TRANSMIT_IR",
                "UNINSTALL_SHORTCUT",
                "UPDATE_DEVICE_STATS",
                "USE_FINGERPRINT",
                "USE_SIP",
                "VIBRATE",
                "WAKE_LOCK",
                "WRITE_APN_SETTINGS",
                "WRITE_CALENDAR",
                "WRITE_CALL_LOG",
                "WRITE_CONTACTS",
                "WRITE_EXTERNAL_STORAGE",
                "WRITE_GSERVICES",
                "WRITE_SECURE_SETTINGS",
                "WRITE_SETTINGS",
                "WRITE_SYNC_SETTINGS",
                "WRITE_VOICEMAIL")
    }

    private val calendarPermissionList by lazy {
        arrayOf("READ_CALENDAR",
                "WRITE_CALENDAR")
    }
    private val cameraPermissionList by lazy {
        arrayOf("CAMERA")
    }

    private val contactsPermissionList by lazy {
        arrayOf("ACCOUNT_MANAGER",
                "GET_ACCOUNTS",
                "READ_CONTACTS",
                "WRITE_CONTACTS")
    }

    private val locationPermissionList by lazy {
        arrayOf("ACCESS_COARSE_LOCATION",
                "ACCESS_FINE_LOCATION",
                "ACCESS_LOCATION_EXTRA_COMMANDS",
                "INSTALL_LOCATION_PROVIDER",
                "LOCATION_HARDWARE")
    }

    private val microphonePermissionList by lazy {
        arrayOf("RECORD_AUDIO")
    }

    private val phonePermissionList by lazy {
        arrayOf("ADD_VOICEMAIL",
                "ANSWER_PHONE_CALLS",
                "CALL_PHONE",
                "MANAGE_OWN_CALLS",
                "PROCESS_OUTGOING_CALLS",
                "READ_CALL_LOG",
                "READ_PHONE_NUMBERS",
                "READ_PHONE_STATE",
                "USE_SIP",
                "WRITE_CALL_LOG")
    }

    private val sensorsPermissionList by lazy {
        arrayOf("USE_FINGERPRINT",
                "BODY_SENSORS")
    }

    private val smsPermissionList by lazy {
        arrayOf("READ_SMS",
                "RECEIVE_MMS",
                "RECEIVE_SMS",
                "RECEIVE_WAP_PUSH",
                "SEND_SMS")
    }

    private val storagePermissionList by lazy {
        arrayOf("WRITE_EXTERNAL_STORAGE",
                "READ_EXTERNAL_STORAGE")
    }

    val networkPermissionList by lazy {
        arrayOf("ACCESS_NETWORK_STATE",
                "ACCESS_WIFI_STATE",
                "CHANGE_NETWORK_STATE",
                "CHANGE_WIFI_STATE",
                "INTERNET",
                "com.google.android.c2dm.permission.RECEIVE")
    }

    val other1PermissionList by lazy {
        arrayOf("DISABLE_KEYGUARD",
                "NFC",
                "RECEIVE_BOOT_COMPLETED",
                "SYSTEM_ALERT_WINDOW",
                "VIBRATE",
                "WAKE_LOCK",
                "FOREGROUND_SERVICE",
                "USE_BIOMETRIC")
    }

    val removedPermissionList by lazy {
        arrayOf("READ_PROFILE",
                "WRITE_PROFILE",
                "READ_SOCIAL_STREAM",
                "WRITE_SOCIAL_STREAM",
                "READ_USER_DICTIONARY",
                "WRITE_USER_DICTIONARY",
                "WRITE_SMS",
                "com.android.browser.permission.READ_HISTORY_BOOKMARKS",
                "com.android.browser.permission.WRITE_HISTORY_BOOKMARKS",
                "AUTHENTICATE_ACCOUNTS",
                "MANAGE_ACCOUNTS",
                "USE_CREDENTIALS",
                "SUBSCRIBED_FEEDS_READ",
                "SUBSCRIBED_FEEDS_WRITE",
                "FLASHLIGHT")
    }

    val dangerousGroup by lazy {
        arrayOf("CALENDAR", "CAMERA", "CONTACTS",
                "LOCATION", "MICROPHONE", "PHONE",
                "SENSORS", "SMS", "STORAGE")
    }

    private val groupList by lazy {
        arrayOf(Pair("CALENDAR", calendarPermissionList),
                Pair("CAMERA", cameraPermissionList),
                Pair("CONTACTS", contactsPermissionList),
                Pair("LOCATION", locationPermissionList),
                Pair("MICROPHONE", microphonePermissionList),
                Pair("PHONE", phonePermissionList),
                Pair("SENSORS", sensorsPermissionList),
                Pair("SMS", smsPermissionList),
                Pair("STORAGE", storagePermissionList))
    }

    fun databaseInitialization(db: SupportSQLiteDatabase) {
        // add permissions
        val ids = addContent(db, permissionsList, "Permission")
        // add groups and their permissions relations
        groupList.forEach { (group, list) ->
            addGroup(db, ids, group, list)
        }
    }

    fun permissionStringToPermStr(permission: String): String = when {
        permission.startsWith(DatabaseUtil.androidPermissionPrefix) -> {
            permission.substring(DatabaseUtil.androidPermissionPrefix.length)
        }
        permission.startsWith(DatabaseUtil.voicemailPermissionPrefix) -> {
            permission.substring(DatabaseUtil.voicemailPermissionPrefix.length)
        }
        else -> {
            permission
        }
    }

    private fun addContent(db: SupportSQLiteDatabase,
                           content: Array<String>, table: String): List<Long> {
        val cv = ContentValues()
        val ids = ArrayList<Long>()

        db.transaction {
            content.forEach {
                cv.put("name", it)
                ids.add(db.insert(table, OnConflictStrategy.REPLACE, cv))
            }
        }

        return ids
    }

    private fun addGroup(db: SupportSQLiteDatabase, permissionIds: List<Long>,
                         group: String, content: Array<String>) {
        db.transaction {
            val cv = ContentValues()

            cv.put("name", group)
            val groupId = db.insert("PermissionGroup", OnConflictStrategy.REPLACE, cv)

            cv.clear()
            content.forEach { permission ->
                cv.put("permissionId", permissionIds[permissionsList.indexOf(permission)])
                cv.put("permissionGroupId", groupId)
                db.insert("PermGroupRelation", OnConflictStrategy.REPLACE, cv)
            }
        }
    }

    private fun SupportSQLiteDatabase.transaction(action: () -> Unit) {
        beginTransaction()
        action()
        setTransactionSuccessful()
        endTransaction()
    }
}