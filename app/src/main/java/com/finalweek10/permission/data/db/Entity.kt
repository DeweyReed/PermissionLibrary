package com.finalweek10.permission.data.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import com.finalweek10.permission.data.source.PermRepository

/**
 * Created on 2017/9/12.
 */

@Entity(indices = [(Index(value = ["packageName"], unique = true))])
data class Application(
        @PrimaryKey(autoGenerate = true)
        val _id: Int,
        val packageName: String,
        val label: String,
        val versionCode: Int,
        val versionName: String,
        val isSystem: Boolean,
        val more: String) {
    fun isEmpty() = this == emptyApp

    companion object {
        val emptyApp by lazy {
            Application(PermRepository.NO_ID, "", "", 0, "", false, "")
        }
    }
}

@Entity(indices = [(Index(value = ["name"], unique = true))])
data class Permission(
        @PrimaryKey(autoGenerate = true)
        val _id: Int,
        val name: String
)

@Entity(indices = [(Index(value = ["name"], unique = true))])
data class PermissionGroup(
        @PrimaryKey(autoGenerate = true)
        val _id: Int,
        val name: String
)

@Entity(indices = [(Index(value = arrayOf("applicationId", "permission"),
        unique = true))],
        foreignKeys = [(ForeignKey(entity = Application::class,
                parentColumns = arrayOf("_id"),
                childColumns = arrayOf("applicationId")))])
data class AppPermRelation(
        @PrimaryKey(autoGenerate = true)
        val _id: Int,
        val applicationId: Int,
        // should be Int. However, to deal with permissions not included in our db,
        // we need to ensure this foreign key by hand
        val permission: String
)

@Entity(indices = [(Index(value = arrayOf("permissionId", "permissionGroupId"),
        unique = true))],
        foreignKeys = [(ForeignKey(entity = Permission::class,
                parentColumns = arrayOf("_id"),
                childColumns = arrayOf("permissionId"))), (ForeignKey(entity = PermissionGroup::class,
                parentColumns = arrayOf("_id"),
                childColumns = arrayOf("permissionGroupId")))])
data class PermGroupRelation(
        @PrimaryKey(autoGenerate = true)
        val _id: Int,
        val permissionId: Int,
        val permissionGroupId: Int
)

data class OldAppInfo(
        val _id: Int,
        val versionCode: Int)