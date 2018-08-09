package com.finalweek10.permission.data.model

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable

/**
 * Created on 2017/10/16.
 */

class ApkFile(
        val name: String,
        val path: String,
        val permCount: Int,
        val icon: Drawable?) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readInt(),
            null
    )

    fun isEmpty() = this.name == "null" && this.path == ""
            && this.permCount == 0 && this.icon == null

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeString(path)
        writeInt(permCount)
    }

    companion object {
        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<ApkFile> = object : Parcelable.Creator<ApkFile> {
            override fun createFromParcel(source: Parcel): ApkFile = ApkFile(source)
            override fun newArray(size: Int): Array<ApkFile?> = arrayOfNulls(size)
        }

        val emptyFile by lazy { ApkFile("null", "", 0, null) }
    }
}