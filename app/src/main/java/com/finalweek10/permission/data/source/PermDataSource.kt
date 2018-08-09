package com.finalweek10.permission.data.source

import android.content.Context
import android.net.Uri
import com.finalweek10.permission.data.db.Application
import com.finalweek10.permission.data.db.Permission
import com.finalweek10.permission.data.db.PermissionGroup
import com.finalweek10.permission.data.model.ApkFile
import java.util.concurrent.Callable

/**
 * Created on 2017/9/15.
 * Works as a Model
 */
interface PermDataSource {
    // read application data
    fun getApplicationsAndTheirPermissionCountWithGroupId(groupId: Int = PermRepository.NO_ID):
            Callable<ArrayList<Pair<Application, Int>>>

    // read permissionGroup data
    fun getAllGroupsAndTheirApplicationCount(): Callable<List<Pair<PermissionGroup, Int>>>

    // single application
    fun getPermissionsWithAppId(id: Int): Callable<List<Pair<Permission, PermissionGroup>>>

    // order for app's permissions
    fun getAllGroups(): Callable<List<PermissionGroup>>

    fun updateDatabase(context: Context, force: Boolean): Callable<Boolean>

    // add or remove one app
    fun addOneApp(context: Context, packageName: String): Callable<Application>

    fun removeOneApp(packageName: String): Callable<Application>

    // apk
    fun getAllStoredApks(context: Context): Callable<List<Pair<ApkFile, Application>>>

    fun getApkFromUri(context: Context, uri: Uri): Callable<Pair<ApkFile, Application>>

    fun getPermissionsWithApkFile(context: Context, apkFile: ApkFile)
            : Callable<List<Pair<Permission, PermissionGroup>>>
}