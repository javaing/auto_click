package com.crtech.autoclick

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.crtech.autoclick.CheckUrlService.Companion.mService
import java.util.*
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity() {
    private val TAG = javaClass.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        findViewById<TextView>(R.id.tv).setOnClickListener {
            mService?.toast("click...")

            val url = "http://g95ekwxn.mengxids.com/"
            //val url = "http://z5pg4xt04o.huolixc.com/?linkid=7&pop=1"
            checkURL(url)
        }

        if (!CheckUrlService.isStart) {
            try {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            } catch (e: Exception) {
                startActivity(Intent(Settings.ACTION_SETTINGS))
                e.printStackTrace()
            }
        }
    }

    fun checkURL(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)

        postRun(3000) {
            val ces: AccessibilityNodeInfo? = mService?.findFirst(AbstractTF.newWebText("无法", false))
            if (ces == null) {
                //mService?.toast("找「$target」失败")
            } else {
                mService?.toast("控件text:${ces.text}")
            }
        }

        postRun(4000) {
            val ces: AccessibilityNodeInfo? = mService?.findFirst(AbstractTF.newWebText("页面", false))
            if (ces == null) {
                mService?.toast("無符合(页面)")
            } else {
                mService?.toast("控件text:${ces.text}")
            }
        }

        postRun(5000) {
            val ces: AccessibilityNodeInfo? =
                mService?.findFirst(AbstractTF.newWebText("刷新", false))
            if (ces == null) {
                mService?.toast("無符合(刷新)")
            } else {
                mService?.toast( "控件text:${ces.text}")
            }
        }

        postRun(6000) {
            val ces: AccessibilityNodeInfo? = mService?.findFirst(AbstractTF.newWebText("訪問", false))
            if (ces == null) {
                mService?.toast("無符合(訪問)")
            } else {
                mService?.toast("控件text:${ces.text}")
            }
        }
    }

    private fun postRun(delay:Long, callback:()->Unit) {
        Timer("SettingUp", false).schedule(delay) {
            callback.invoke()
        }
    }
}