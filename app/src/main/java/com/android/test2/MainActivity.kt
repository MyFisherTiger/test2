package com.android.test2

import android.content.Intent
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onStart() {
        super.onStart()
        btn_hw1.setOnClickListener {
            startActivity(Intent(this@MainActivity,HW1EncoderActivity::class.java))
        }

        btn_hw2.setOnClickListener {
            startActivity(Intent(this@MainActivity,HW2EncoderActivity::class.java))
        }
    }
}
