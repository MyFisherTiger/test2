package com.android.test2

import android.Manifest
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    //权限
    protected lateinit var permissionManager: PermissionManager
    protected lateinit var permissionRequestListener: OnPermissionRequestListener


    interface OnPermissionRequestListener {
        fun onSuccess() //请求权限成功时回调

        fun onFailure() //请求权限失败且不再提醒时回调
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        permissionManager = PermissionManager.with(this)
    }

    /**
     * 请求权限
     *
     * @param permissions                 要请求权限（String[]）
     * @param onPermissionRequestListener 请求成功或者失败回调
     */
    fun askPermission(
        permissions: Array<String>,
        onPermissionRequestListener: OnPermissionRequestListener
    ) {
        permissionManager!!.setNecessaryPermissions(*permissions)
        permissionRequestListener = onPermissionRequestListener
        if (permissionManager!!.isLackPermission()) {
            permissionManager!!.requestPermissions()
        } else {
            permissionRequestListener.onSuccess()
        }
    }

    /**
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val canShowPermissionCode = permissionManager!!.getShouldShowRequestPermissionsCode()

        if (requestCode == PermissionManager.PERMISSION_REQUEST_CODE) {//PERMISSION_REQUEST_CODE为请求权限的请求值
            //有必须权限选择了禁止
            if (canShowPermissionCode == PermissionManager.EXIST_NECESSARY_PERMISSIONS_PROHIBTED) {
                permissionManager!!.requestPermissions()
            } //有必须权限选择了禁止不提醒
            else if (canShowPermissionCode == PermissionManager.EXIST_NECESSARY_PERMISSIONS_PROHIBTED_NOT_REMIND) {
                permissionRequestListener.onFailure()
            } else {
                permissionRequestListener.onSuccess()
            }
        }


    }

    protected abstract fun  getLayoutId():Int

}