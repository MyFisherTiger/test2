package com.android.test2

import android.Manifest
import android.os.Handler
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.kjs.medialibrary.LogMedia
import com.kjs.medialibrary.sound.AudioRecorder
import com.kjs.medialibrary.sound.encoder.AACEncoder
import com.kjs.medialibrary.sound.encoder.WAVEncoder
import com.kjs.medialibrary.video.hw2.VideoRecorder

class HW2EncoderActivity : BaseActivity() {
    private var context = this
    // 项目的必须权限，没有这些权限会影响项目的正常运行
    private val PERMISSIONS = arrayOf<String>(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        //Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO

    )

    override fun getLayoutId(): Int {
        return R.layout.activity_hw2encoder
    }

    override fun onStart() {
        super.onStart()
        askPermission(PERMISSIONS, object : OnPermissionRequestListener {
            override fun onSuccess() {
                LogMedia.info("获取权限成功")
                //testCamera()
                testAudio()
            }

            override fun onFailure() {
                LogMedia.info("获取权限失败")
                PermissionManager.startAppSettings(this@HW2EncoderActivity)
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
                if (videoRecorder != null) {
                    videoRecorder.StopRecordVideo()

                }
            }

            override fun surfaceCreated(p0: SurfaceHolder?) {
                Log.e("", "SurfaceView创建")
                videoRecorder.init(context, p0)
                //videoRecorder.setVideoEncoder(H264Encoder())
                videoRecorder.startRecordVideo()

                Handler().postDelayed(Runnable {
                    videoRecorder.StopRecordVideo()
                }, 10000)
            }

        })


    }

    private fun testAudio() {
        LogMedia.info("录制10秒的声音")
        var audioRecorder = AudioRecorder()
        audioRecorder.setEncoder(AACEncoder())
        //audioRecorder.setEncoder(AMREncoder())
        //audioRecorder.setEncoder(WAVEncoder())
        audioRecorder.start()
        Handler().postDelayed(Runnable {
            audioRecorder.stop()
        }, 10000)
    }
}

