package com.crtech.autoclick

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.crtech.autoclick.CheckUrlService.Companion.mService


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        findViewById<TextView>(R.id.tv).setOnClickListener {
            toast("click...")

            val url = "http://u6yfmb.dearcoo.com/?linkid=7&pop=1"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)

            val handler = Handler()
            handler.postDelayed({
                mService?.dispatchGestureClick(870, 345)
            }, 1000)

            val handler2 = Handler()
            handler2.postDelayed({
                mService?.dispatchGestureClick(500, 340)
            }, 9000)


            val ces: AccessibilityNodeInfo? = mService?.findFirst(AbstractTF.newText("下载app", true))
            if (ces == null) {
                toast("找测试控件失败")
            } else {
                mService?.clickView(ces)
            }
        }

        val handler = Handler()
        handler.postDelayed({
            findViewById<TextView>(R.id.tv).performClick()
        }, 3000)

        if (!CheckUrlService.isStart) {
            try {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            } catch (e: Exception) {
                startActivity(Intent(Settings.ACTION_SETTINGS))
                e.printStackTrace()
            }
        }
    }
}