@file:Suppress("MemberVisibilityCanBePrivate")

package com.finalweek10.permission.data.model

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.IntDef
import com.chad.library.adapter.base.entity.SectionEntity

/**
 * Created on 2017/9/14.
 */

@Suppress("MemberVisibilityCanPrivate")
data class VisibleGroup(
        val _id: Int,
        // equals Entity.PermissionGroup.name
        val simpleName: String,
        // from resources and to different languages
        val descriptionName: String,
        val icon: Drawable? = null,
        val appCount: Int
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readString(),
            source.readString(),
            appCount = source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(_id)
        writeString(simpleName)
        writeString(descriptionName)
        writeInt(appCount)
    }

    companion object {
        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<VisibleGroup> = object : Parcelable.Creator<VisibleGroup> {
            override fun createFromParcel(source: Parcel): VisibleGroup = VisibleGroup(source)
            override fun newArray(size: Int): Array<VisibleGroup?> = arrayOfNulls(size)
        }
    }
}

@IntDef(PERM_ON, PERM_OFF, PERM_NONE)
@Retention(AnnotationRetention.SOURCE)
annotation class PermState

const val PERM_ON = 0
const val PERM_OFF = 1
const val PERM_NONE = 2

data class VisiblePerm(
        val _id: Int,
        // equals Entity.Permission.name
        val simpleName: String,
        // from resources
        val permLabel: String,
        val permDescription: String,
        @PermState var state: Int)

class SectionPerm : SectionEntity<VisiblePerm> {
    var count = 0
    var dangerous = false

    constructor(isHeader: Boolean, header: String, c: Int, d: Boolean) : super(isHeader, header) {
        this.count = c
        this.dangerous = d
    }

    constructor(t: VisiblePerm) : super(t)
}

data class VisibleApp(
        val _id: Int,
        val packageName: String,
        val label: String,
        val versionCode: Int,
        val versionName: String,
        val isSystem: Boolean,
        val more: String,
        val icon: Drawable? = null,
        val permissionCount: Int)
    : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readString(),
            1 == source.readInt(),
            source.readString(),
            null,
            source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(_id)
        writeString(packageName)
        writeString(label)
        writeInt(versionCode)
        writeString(versionName)
        writeInt((if (isSystem) 1 else 0))
        writeString(more)
        writeInt(permissionCount)
    }

    companion object {
        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<VisibleApp> = object : Parcelable.Creator<VisibleApp> {
            override fun createFromParcel(source: Parcel): VisibleApp = VisibleApp(source)
            override fun newArray(size: Int): Array<VisibleApp?> = arrayOfNulls(size)
        }
    }
}