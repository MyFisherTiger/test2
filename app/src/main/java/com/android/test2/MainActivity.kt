package com.android.test2

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.kjs.medialibrary.LogMedia
import com.kjs.medialibrary.sound.AudioRecorder
import com.kjs.medialibrary.sound.encoder.WAVEncoder
import com.kjs.medialibrary.video.VideoRecorder
import com.kjs.medialibrary.video.encoder.H264Encoder


class MainActivity : AppCompatActivity() {
    private var context = this
    //权限
    protected lateinit var permissionManager: PermissionManager
    protected lateinit var permissionRequestListener: OnPermissionRequestListener
    // 项目的必须权限，没有这些权限会影响项目的正常运行
    private val PERMISSIONS = arrayOf<String>(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        //Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CAMERA
    )

    interface OnPermissionRequestListener {
        fun onSuccess() //请求权限成功时回调

        fun onFailure() //请求权限失败且不再提醒时回调
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissionManager = PermissionManager.with(this)
    }

    override fun onStart() {
        super.onStart()
        askPermission(PERMISSIONS, object : OnPermissionRequestListener {
            override fun onSuccess() {
                LogMedia.info("获取权限成功")
                testCamera()
            }

            override fun onFailure() {
                LogMedia.info("获取权限失败")
                PermissionManager.startAppSettings(this@MainActivity)
            }

        })

    }

    var videoRecorder = VideoRecorder()

    //测试摄像头
    private fun testCamera() {
        var sv: SurfaceView = findViewById<SurfaceView>(R.id.sv_camera) as SurfaceView;
        var holder: SurfaceHolder = sv.holder
        sv.setOnClickListener {
            finish()
        }
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
                Log.e("", "SurfaceView改变")
            }

            override fun surfaceDestroyed(p0: SurfaceHolder?) {
                Log.e("", "SurfaceView销毁")
                videoRecorder.StopRecordVideo()
            }

            override fun surfaceCreated(p0: SurfaceHolder?) {
                Log.e("", "SurfaceView创建")

                videoRecorder.init(context, p0)
                videoRecorder.setVideoEncoder(H264Encoder())
                videoRecorder.startRecordVideo()

                Handler().postDelayed(Runnable {
                    videoRecorder.StopRecordVideo()
                }, 10000)
            }

        })


    }

    private fun testAudio() {
        var audioRecorder = AudioRecorder()
        //audioRecorder.setEncoder(AACEncoder())
        //audioRecorder.setEncoder(AMREncoder())
        audioRecorder.setEncoder(WAVEncoder())
        audioRecorder.start()
        Handler().postDelayed(Runnable {
            audioRecorder.stop()
        }, 20000)
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
}
