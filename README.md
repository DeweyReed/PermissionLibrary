<div align="center">
  <img src="https://github.com/DeweyReed/PermissionLibrary/blob/master/art/app_icon.png?raw=true" height="128" />
</div>

<h1 align="center">Permission Library</h1>

<div align="center">
  <strong>List Device's Apps and Their Permissions Details</strong>
</div>
</br>
<div align="center">
    <a href="https://github.com/DeweyReed/PermissionLibrary/releases">
        <img src="https://img.shields.io/badge/Download-Github-green.svg"/>
    </a>
    <a href="https://www.coolapk.com/apk/162565">
        <img src="https://img.shields.io/badge/Download-CoolApk-green.svg"/>
    </a>
    </br>
    <a href="https://github.com/DeweyReed/PermissionLibrary/blob/master/README_ZH.md">
        <img src="https://img.shields.io/badge/Translation-%E4%B8%AD%E6%96%87-red.svg">
    </a>
</div>
</br>

## Deprecated

I don't have time for maintaining this project. Feel free to fork this project and move on.

## Screenshot

||||
|:-:|:-:|:-:|
|![screenshot1](https://github.com/DeweyReed/PermissionLibrary/blob/master/art/screenshot_1.webp?raw=true)|![screenshot2](https://github.com/DeweyReed/PermissionLibrary/blob/master/art/screenshot_2.webp?raw=true)|![screenshot3](https://github.com/DeweyReed/PermissionLibrary/blob/master/art/screenshot_3.webp?raw=true)|

## Introduction

MVP + Room + Dagger2 + RxJava2

However, since I wrote this app for practicing, the implementation and codes in this project **are not** guaranteed to be best practices.

## Flavors

There're two flavors: `google` and `other`.

`other` flavor supports sorting apps with Chinese Pinyin.

## Release Procedure

1. App: comment DEBUG block
1. build.gradle: disable development assistant dependencies
1. Change version code

## Resources

[Official Docs](https://developer.android.com/guide/topics/permissions/index.html)

[Material Design Related to Permissions](https://material.io/guidelines/patterns/permissions.html)

[Official Permission List](https://developer.android.com/reference/android/Manifest.permission.html)

[An Open Sourced App](https://code.google.com/archive/p/android-permission-explorer/)

[All Permissions and Their Details](https://github.com/aosp-mirror/platform_frameworks_base/blob/master/core/res/AndroidManifest.xml)

[Some Permissions' Details](http://androidpermissions.com/)

## License

[MIT License](https://github.com/DeweyReed/PermissionLibrary/blob/master/LICENSE)