package com.android.test2

import android.content.Intent
import com.kjs.medialibrary.nactive.NativeFFMPEG
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {
    var nativeFFMPEG=NativeFFMPEG()

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onStart() {
        super.onStart()
        NativeFFMPEG.loadFFMEPGLib()//加载ffmpeg库
        btn_hw1.setOnClickListener {
            startActivity(Intent(this@MainActivity,HW1EncoderActivity::class.java))
        }

        btn_hw2.setOnClickListener {
            startActivity(Intent(this@MainActivity,HW2EncoderActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        tv_ffmpeg.text=nativeFFMPEG.stringFromJNI()
    }
}
