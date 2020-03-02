package com.android.test2

import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kjs.medialibrary.video.hw.recorder.MediaAudioEncoder
import com.kjs.medialibrary.video.hw.recorder.MediaEncoder
import com.kjs.medialibrary.video.hw.recorder.MediaMuxerWrapper
import com.kjs.medialibrary.video.hw.recorder.MediaVideoEncoder
import kotlinx.android.synthetic.main.activity_hw1_encoder.*
import java.io.File
import java.io.IOException

class HW1EncoderActivity : AppCompatActivity() {
    private var mMuxer: MediaMuxerWrapper? = null
    private var mMediaVideoEncoder: MediaVideoEncoder? = null
    private var mMediaAudioEncoder: MediaAudioEncoder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hw1_encoder)
    }

    override fun onStart() {
        super.onStart()
        iv_startCamera.setOnClickListener(View.OnClickListener {
            if (mMuxer == null) {
                startRecording()
                iv_startCamera.setImageResource(R.drawable.shape_pause)
            } else if (mMediaVideoEncoder != null && !mMediaVideoEncoder!!.getState()) {
                mMediaVideoEncoder!!.pause()
                mMediaAudioEncoder!!.pause()
                iv_startCamera.setImageResource(R.drawable.shape_start)
            } else if (mMediaVideoEncoder != null && mMediaVideoEncoder!!.getState()) {
                mMediaVideoEncoder!!.resume()
                mMediaAudioEncoder!!.resume()
                iv_startCamera.setImageResource(R.drawable.shape_pause)
            }
        })

        iv_saveVideo.setOnClickListener(View.OnClickListener {
            stopRecord()
            iv_startCamera.setImageResource(R.drawable.shape_start)
            finish()
        })
    }

    private fun stopRecord() {
        mMuxer?.let {
            it.stopRecording()
        }
        //mMuxer=null
    }

    private fun startRecording() {
        try {
            val savePath =
                getPath(System.currentTimeMillis().toString() + ".mp4")
            mMuxer = MediaMuxerWrapper(
                savePath
            ) // if you record audio only, ".m4a" is also OK.
            if (true) { // for video capturing
                mMediaVideoEncoder = MediaVideoEncoder(
                    mMuxer,
                    mMediaEncoderListener, cameraView.getVideoWidth(),
                    cameraView.getVideoHeight()
                )
            }
            if (true) { // for audio capturing
                mMediaAudioEncoder = MediaAudioEncoder(
                    mMuxer,
                    mMediaEncoderListener
                )
            }
            mMuxer!!.prepare()
            mMuxer!!.startRecording()
        } catch (e: IOException) {
        }
    }

    private val mMediaEncoderListener: MediaEncoder.MediaEncoderListener =
        object : MediaEncoder.MediaEncoderListener {
            override fun onPrepared(encoder: MediaEncoder?) {
                if (encoder is MediaVideoEncoder) {
                    cameraView.setVideoEncoder(encoder as MediaVideoEncoder?)
                }
            }

            override fun onStopped(encoder: MediaEncoder?) {
                if (encoder is MediaVideoEncoder) {
                    cameraView.setVideoEncoder(null)
                }
            }
        }


    fun getPath(fileName: String): String {
        val p = getBaseFolder()
        val f = File(p)
        return if (!f.exists() && !f.mkdirs()) {
            getBaseFolder() + fileName
        } else p + fileName
    }

    fun getBaseFolder(): String {
        var baseFolder =
            Environment.getExternalStorageDirectory().toString() + "/HW2Video/"
        val f = File(baseFolder)
        if (!f.exists()) {
            val b = f.mkdirs()
            if (!b) {
                baseFolder =
                    application.getExternalFilesDir(null)!!.getAbsolutePath().toString() + "/"
            }
        }
        return baseFolder
    }

    override fun onBackPressed() {
        stopRecord()
        super.onBackPressed()
    }
}
