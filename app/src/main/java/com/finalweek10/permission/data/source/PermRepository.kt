package com.finalweek10.permission.data.source

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import com.finalweek10.permission.R
import com.finalweek10.permission.data.db.*
import com.finalweek10.permission.data.db.Application.Companion.emptyApp
import com.finalweek10.permission.data.helper.PreferenceHelper
import com.finalweek10.permission.data.helper.SpecialFileFilter
import com.finalweek10.permission.data.model.ApkFile
import com.finalweek10.permission.extension.drawable
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FileFilterUtils
import java.io.File
import java.util.concurrent.Callable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created on 2017/9/15.
 */

@Singleton
class PermRepository @Inject constructor(
        private val mDatabase: PermissionDatabase,
        private val mPrefHelper: PreferenceHelper) : PermDataSource {

    companion object {
        const val NO_ID = -1
        val GROUP_NETWORK = PermissionGroup(NO_ID - 1, "NETWORK")
        val GROUP_OTHER1 = PermissionGroup(NO_ID - 5, "OTHER1")
        val GROUP_OTHER = PermissionGroup(NO_ID, "OTHER")
        val GROUP_LAUNCHER = PermissionGroup(NO_ID - 7, "LAUNCHER")
        val GROUP_GUESS = PermissionGroup(NO_ID - 15, "GUESS")
        val GROUP_REMOVED = PermissionGroup(NO_ID - 10, "REMOVED")

        private val emptyPair by lazy { Pair(ApkFile.emptyFile, emptyApp) }
    }

    private val permissionDao = mDatabase.permissionDao()
    private val applicationDao = mDatabase.applicationDao()
    private val permissionGroupDao = mDatabase.groupDao()
    private val appPermDao = mDatabase.appPermDao()

    /**
     * First fragment data inflater
     */
    override fun getApplicationsAndTheirPermissionCountWithGroupId(groupId: Int):
            Callable<ArrayList<Pair<Application, Int>>> = Callable {
        val pairs = arrayListOf<Pair<Application, Int>>()
        val appsList =
                if (groupId == NO_ID) applicationDao.getAllApplications(normalOrSystem())
                else applicationDao.getApplicationsWithGroupId(groupId, normalOrSystem())
        appsList.forEach { app ->
            pairs.add(Pair(app,
                    appPermDao.getAppPermRelationCountWithPackageName(app.packageName)))
        }
        pairs
    }

    /**
     * Second fragment data inflater
     */
    override fun getAllGroupsAndTheirApplicationCount():
            Callable<List<Pair<PermissionGroup, Int>>> = Callable {
        val pairs = arrayListOf<Pair<PermissionGroup, Int>>()
        val nOS = normalOrSystem()
        permissionGroupDao.getAllGroups().forEach { group ->
            pairs.add(Pair(group, applicationDao.getApplicationCountWithGroupId(group._id, nOS)))
        }
        pairs
    }

    override fun getPermissionsWithAppId(id: Int): Callable<List<Pair<Permission, PermissionGroup>>> = Callable {
        val permissions = arrayListOf<Pair<Permission, PermissionGroup>>()
        appPermDao.getAppPermRelationsWithAppId(id).forEach { relation ->
            val perm = relation.permission
            val idOrNull = perm.toIntOrNull()
            if (idOrNull == null) {
                permissions.add(Pair(Permission(NO_ID, perm), GROUP_OTHER))
            } else {
                permissions.add(Pair(permissionDao.getPermissionWithId(idOrNull),
                        permissionGroupDao.getGroupWithPermissionId(idOrNull) ?: GROUP_OTHER))
            }
        }
        permissions
    }

    override fun getAllGroups(): Callable<List<PermissionGroup>> = Callable {
        permissionGroupDao.getAllGroups().toMutableList().apply {
            add(GROUP_OTHER)
        }
    }

    override fun updateDatabase(context: Context, force: Boolean) = Callable {
        val packageManager = context.applicationContext.packageManager
        val appInfoArray = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        var isUpdated = false
        mDatabase.runInTransaction {
            if (force || applicationDao.getApplicationCount() > appInfoArray.size) {
                // there are some apps haven being deleted
                // so we clean the db
                appPermDao.deleteAllRelations()
                applicationDao.deleteAllApplications()
            }
            for (index in 0 until appInfoArray.size) {
                val appInfo = appInfoArray[index]
                val packageName = appInfo.packageName
                val pi = try {
                    packageManager.getPackageInfo(packageName,
                            PackageManager.GET_META_DATA or PackageManager.GET_PERMISSIONS)
                } catch (e: PackageManager.NameNotFoundException) {
                    continue
                }
                val oldAppInfo = applicationDao.getOldAppInfo(packageName)
                val versionCode = pi.versionCode
                // if there is no info or version code is too low
                if (!TextUtils.isEmpty(packageName)
                        && (oldAppInfo == null
                                || oldAppInfo.versionCode != versionCode)) {
                    isUpdated = true
                    // delete its all info if existing
                    oldAppInfo?.run {
                        appPermDao.deleteAppPermissions(_id)
                        applicationDao.deleteAppWithId(_id)
                    }
                    val appId = applicationDao.addApplication(Application(
                            0, packageName,
                            packageManager.getApplicationLabel(appInfo)?.toString() ?: "",
                            versionCode, pi.versionName ?: "",
                            appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1,
                            ""))
                    pi.requestedPermissions?.forEach { permission ->
                        var permStr = DatabaseUtil.permissionStringToPermStr(permission)
                        val id: Long? = permissionDao.getIdWithName(permStr)
                        id?.run { permStr = this.toString() }
                        appPermDao.addRelation(AppPermRelation(0, appId.toInt(), permStr))
                    }
                }
            }
        }
        isUpdated
    }

    override fun addOneApp(context: Context, packageName: String): Callable<Application> = Callable {
        var addedApp: Application = emptyApp
        var pmSize = 0

        val packageManager = context.packageManager
        val pi = try {
            packageManager.getPackageInfo(packageName,
                    PackageManager.GET_META_DATA or PackageManager.GET_PERMISSIONS)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        // if there is no info or version code is too low
        var appId = 0L
        if (!TextUtils.isEmpty(packageName) && pi != null) {
            val ai: ApplicationInfo? = pi.applicationInfo
            addedApp = Application(0, packageName,
                    packageManager.getApplicationLabel(ai)?.toString() ?: "",
                    pi.versionCode, pi.versionName ?: "",
                    ai?.flags ?: 0 and ApplicationInfo.FLAG_SYSTEM == 1,
                    "")
            appId = applicationDao.addApplication(addedApp)
            pi.requestedPermissions?.apply {
                pmSize = size
                forEach { permission ->
                    var permStr = DatabaseUtil.permissionStringToPermStr(permission)
                    val id: Long? = permissionDao.getIdWithName(permStr)
                    id?.run { permStr = this.toString() }
                    appPermDao.addRelation(AppPermRelation(0, appId.toInt(), permStr))
                }
            }
        }
        addedApp.copy(_id = appId.toInt(), more = pmSize.toString())
    }

    override fun removeOneApp(packageName: String) = Callable<Application> {
        val oldAppInfo = applicationDao.getOldAppInfo(packageName)
        oldAppInfo?.run {
            appPermDao.deleteAppPermissions(_id)
            applicationDao.deleteAppWithId(_id)
        }
        emptyApp
    }

    override fun getAllStoredApks(context: Context): Callable<List<Pair<ApkFile, Application>>> = Callable {
        val apks = arrayListOf<Pair<ApkFile, Application>>()

        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val pm = context.packageManager
            val dict = Environment.getExternalStorageDirectory()
            val iterator = FileUtils.iterateFiles(dict,
                    FileFilterUtils.suffixFileFilter("apk"),
                    SpecialFileFilter(dict))
            while (iterator.hasNext()) {
                fileToInfo(context, pm, iterator.next()).run {
                    if (!first.isEmpty() && !second.isEmpty()) {
                        apks.add(this)
                    }
                }
            }
        }
        apks
    }

    override fun getApkFromUri(context: Context, uri: Uri): Callable<Pair<ApkFile, Application>> = Callable {
        val uriContent = uri.toString()
        when {
            uriContent.startsWith("content") -> return@Callable apkCursorToInfo(context, uri)
            uriContent.startsWith("file") -> return@Callable apkFileToInfo(context, uri)
            else -> return@Callable emptyPair
        }
    }

    override fun getPermissionsWithApkFile(context: Context, apkFile: ApkFile)
            : Callable<List<Pair<Permission, PermissionGroup>>> = Callable {
        val permissions = arrayListOf<Pair<Permission, PermissionGroup>>()
        val pm = context.packageManager
        val pi = pm.getPackageArchiveInfo(apkFile.path,
                PackageManager.GET_META_DATA or PackageManager.GET_PERMISSIONS)
        pi?.requestedPermissions?.forEach { permission ->
            var permStr = DatabaseUtil.permissionStringToPermStr(permission)
            val id: Long? = permissionDao.getIdWithName(permStr)
            id?.run { permStr = this.toString() }
            val idOrNull = permStr.toIntOrNull()
            if (idOrNull == null) {
                permissions.add(Pair(Permission(NO_ID, permStr), GROUP_OTHER))
            } else {
                permissions.add(Pair(permissionDao.getPermissionWithId(idOrNull),
                        permissionGroupDao.getGroupWithPermissionId(idOrNull) ?: GROUP_OTHER))
            }
        }
        permissions
    }

    private fun apkCursorToInfo(context: Context, uri: Uri): Pair<ApkFile, Application> {
        val columnNames = arrayOf(OpenableColumns.DISPLAY_NAME, MediaStore.MediaColumns.DATA)
        val cursor = context.contentResolver.query(uri, columnNames, null,
                null, null, null)
        val apk: Pair<ApkFile, Application>
        if (cursor != null && cursor.moveToFirst()) {
            val title = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            val pathIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
            val path = if (pathIndex != -1) cursor.getString(pathIndex) else null

            val pm = context.packageManager
            if (TextUtils.isEmpty(path)) return emptyPair
            val pi = pm.getPackageArchiveInfo(path,
                    PackageManager.GET_META_DATA or PackageManager.GET_PERMISSIONS)
            if (pi != null) {
                val ai = pi.applicationInfo
                apk = Pair(ApkFile(title, path!!, pi.requestedPermissions?.size ?: 0,

                        if (ai != null) ai.loadIcon(pm)
                        else context.drawable(R.drawable.about_android)),

                        Application(NO_ID, pi.packageName, title,
                                pi.versionCode, pi.versionName ?: "",
                                if (ai != null)
                                    ai.flags and ApplicationInfo.FLAG_SYSTEM == 1
                                else false,
                                ""))
            } else {
                apk = emptyPair
            }
            cursor.close()
        } else {
            apk = emptyPair
        }
        return apk
    }

    private fun apkFileToInfo(context: Context, uri: Uri): Pair<ApkFile, Application> {
        return fileToInfo(context, context.packageManager,
                File(uri.toString().removePrefix("file://")))
    }

    private fun fileToInfo(context: Context, pm: PackageManager, file: File):
            Pair<ApkFile, Application> {
        if (!file.canRead() || !file.isAbsolute) return emptyPair
        val path = file.absolutePath
        val pi = pm.getPackageArchiveInfo(path,
                PackageManager.GET_META_DATA or PackageManager.GET_PERMISSIONS)
        if (pi != null) {
            val ai = pi.applicationInfo
            // fix no icon bug? I don't know why but it works
            ai.sourceDir = path
            ai.publicSourceDir = path
            val title = file.nameWithoutExtension
            return Pair(ApkFile(title, path,
                    pi.requestedPermissions?.size ?: 0,

                    if (ai != null) ai.loadIcon(pm)
                    else context.drawable(R.drawable.ic_error)),

                    Application(NO_ID, pi.packageName, title, pi.versionCode,
                            pi.versionName ?: "",

                            if (ai != null)
                                ai.flags and ApplicationInfo.FLAG_SYSTEM == 1
                            else false,

                            ""))
        } else {
            return emptyPair
        }
    }

    private fun normalOrSystem(): List<Int> {
        val a = arrayListOf<Int>()
        mPrefHelper.showConfig.apply {
            // if disabled, we handle at Presenter
            if (!normal && !system && disabled) return listOf(0, 1)
            if (normal) a.add(0)
            if (system) a.add(1)
        }
        return a.toList()
    }
}