package com.finalweek10.permission.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

/**
 * Created on 2017/9/12.
 */

@Dao
interface ApplicationDao {
    @Query("SELECT * FROM Application WHERE isSystem IN (:showSystem)")
    fun getAllApplications(showSystem: List<Int>): List<Application>

    @Query("SELECT COUNT(DISTINCT Application._id) FROM Application " +
            "JOIN AppPermRelation " +
            "ON Application._id = AppPermRelation.applicationId " +
            "JOIN PermGroupRelation " +
            "ON AppPermRelation.permission = PermGroupRelation.permissionId " +
            "WHERE PermGroupRelation.permissionGroupId = :id " +
            "AND Application.isSystem IN (:showSystem)")
    fun getApplicationCountWithGroupId(id: Int, showSystem: List<Int>): Int

    @Query("SELECT DISTINCT Application._id, Application.packageName, Application.label, " +
            "Application.versionCode, Application.versionName, " +
            "Application.isSystem, Application.more FROM Application " +
            "JOIN AppPermRelation " +
            "ON Application._id = AppPermRelation.applicationId " +
            "JOIN PermGroupRelation " +
            "ON AppPermRelation.permission = PermGroupRelation.permissionId " +
            "WHERE PermGroupRelation.permissionGroupId = :id " +
            "AND Application.isSystem IN (:showSystem)")
    fun getApplicationsWithGroupId(id: Int, showSystem: List<Int>): List<Application>

    // update methods
    @Query("SELECT _id, versionCode FROM Application WHERE packageName = :packageName")
    fun getOldAppInfo(packageName: String): OldAppInfo?

    @Query("DELETE FROM Application WHERE _id = :id")
    fun deleteAppWithId(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addApplication(app: Application): Long

    @Query("SELECT COUNT(*) FROM Application")
    fun getApplicationCount(): Int

    @Query("DELETE FROM Application")
    fun deleteAllApplications()
}

@Dao
interface PermissionDao {
    @Query("SELECT * FROM Permission WHERE _id = :id")
    fun getPermissionWithId(id: Int): Permission

    // update methods
    @Query("SELECT _id FROM Permission WHERE name = :name")
    fun getIdWithName(name: String): Long?
}

@Dao
interface GroupDao {
    @Query("SELECT * FROM PermissionGroup")
    fun getAllGroups(): List<PermissionGroup>

    @Query("SELECT PermissionGroup._id, PermissionGroup.name FROM PermissionGroup " +
            "JOIN PermGroupRelation " +
            "ON PermissionGroup._id = PermGroupRelation.permissionGroupId " +
            "WHERE PermGroupRelation.permissionId = :id")
    fun getGroupWithPermissionId(id: Int): PermissionGroup?
}

@Dao
interface AppPermDao {
    @Query("SELECT COUNT(*) FROM AppPermRelation " +
            "JOIN Application " +
            "ON AppPermRelation.applicationId = Application._id " +
            "WHERE Application.packageName = :packageName")
    fun getAppPermRelationCountWithPackageName(packageName: String): Int


    @Query("SELECT AppPermRelation._id, AppPermRelation.applicationId, AppPermRelation.permission" +
            " FROM AppPermRelation " +
            "JOIN Application " +
            "ON AppPermRelation.applicationId = Application._id " +
            "WHERE Application._id = :id")
    fun getAppPermRelationsWithAppId(id: Int): List<AppPermRelation>

    // update methods
    @Query("DELETE FROM AppPermRelation WHERE applicationId = :id")
    fun deleteAppPermissions(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRelation(relation: AppPermRelation): Long

    @Query("DELETE FROM AppPermRelation")
    fun deleteAllRelations()
}