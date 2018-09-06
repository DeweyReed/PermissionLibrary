<div align="center">
  <img src="https://github.com/DeweyReed/PermissionLibrary/blob/master/art/app_icon.png?raw=true" height="128" />
</div>

<h1 align="center">权限图书馆</h1>

<div align="center">
  <strong>显示设备上的应用及其权限</strong>
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
</div>
</br>

## 截图

||||
|:-:|:-:|:-:|
|![screenshot1](https://github.com/DeweyReed/PermissionLibrary/blob/master/art/screenshot_4.webp?raw=true)|![screenshot2](https://github.com/DeweyReed/PermissionLibrary/blob/master/art/screenshot_5.webp?raw=true)|![screenshot3](https://github.com/DeweyReed/PermissionLibrary/blob/master/art/screenshot_6.webp?raw=true)|

## 简介

MVP + Room + Dagger2 + RxJava2

然而，练手项目**不保证**其实现和代码都是最佳实践。

## Flavors

有两个Flavor: `google`和`other`

`other`增加了根据中文拼音排序应用的功能。

## 发布流程

1. App: 注释DEBUG代码
1. build.gradle: 注释development assistant依赖
1. 修改version code

## Resources

[官方文档](https://developer.android.com/guide/topics/permissions/index.html)

[与权限相关的Material Deisgn](https://material.io/guidelines/patterns/permissions.html)

[官方的权限列表](https://developer.android.com/reference/android/Manifest.permission.html)

[一个开源应用](https://code.google.com/archive/p/android-permission-explorer/)

[所有权限及其内容](https://github.com/aosp-mirror/platform_frameworks_base/blob/master/core/res/AndroidManifest.xml)

[一些权限的描述](http://androidpermissions.com/)

## License

[MIT License](https://github.com/DeweyReed/PermissionLibrary/blob/master/LICENSE)