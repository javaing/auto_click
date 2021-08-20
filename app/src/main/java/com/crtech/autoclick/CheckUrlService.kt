package com.crtech.autoclick

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import java.security.InvalidParameterException


class CheckUrlService : AccessibilityService() {
    private val TAG = javaClass.name

    //初始化
    override fun onServiceConnected() {
        super.onServiceConnected()
        toast("锁定中...");
        mService = this
    }

    //实现辅助功能
    override fun onAccessibilityEvent(event: AccessibilityEvent) {}
    override fun onInterrupt() {
        toast("功能被迫中断");
        mService = null
    }

    override fun onDestroy() {
        super.onDestroy()
        toast("功能已关闭");
        mService = null
    }

    /**
     * 点击指定位置
     * 注意7.0以上的手机才有此方法，请确保运行在7.0手机上
     */
    @RequiresApi(24)
    fun dispatchGestureClick(x: Int, y: Int) {
        val path = Path()
        path.moveTo(x - 1f, y - 1f)
        path.lineTo(x + 1f, y + 1f)
        dispatchGesture(
            GestureDescription.Builder().addStroke(StrokeDescription(path, 0, 100)).build(),
            null,
            null
        )
        toast("click...($x, $y)")
    }

    /**
     * 点击该控件
     *
     * @return true表示点击成功
     */
    fun clickView(nodeInfo: AccessibilityNodeInfo?): Boolean {
        if (nodeInfo != null) {
            if (nodeInfo.isClickable) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                return true
            } else {
                val parent = nodeInfo.parent
                if (parent != null) {
                    val b = clickView(parent)
                    parent.recycle()
                    if (b) return true
                }
            }
        }
        return false
    }

    /**
     * 查找第一个匹配的控件
     *
     * @param tfs 匹配条件，多个AbstractTF是&&的关系，如：
     * AbstractTF.newContentDescription("表情", true),AbstractTF.newClassName(AbstractTF.ST_IMAGEVIEW)
     * 表示描述内容是'表情'并且是imageview的控件
     */
    @Nullable
    fun findFirst(vararg tfs: AbstractTF<Any>): AccessibilityNodeInfo? {
        if (tfs.size == 0) throw InvalidParameterException("AbstractTF不允许传空")
        val rootInfo = rootInActiveWindow ?: return null
        var idTextTFCount = 0
        var idTextIndex = 0
        for (i in 0 until tfs.size) {
            if (tfs[i] is AbstractTF.IdTextTF) {
                idTextTFCount++
                idTextIndex = i
            }
        }
        return when (idTextTFCount) {
            0 -> {
                val returnInfo: AccessibilityNodeInfo? = findFirstRecursive(rootInfo, *tfs)
                rootInfo.recycle()
                returnInfo
            }
            1 -> if (tfs.size == 1) {
                val returnInfo2: AccessibilityNodeInfo? =
                    (tfs[idTextIndex] as AbstractTF.IdTextTF).findFirst(rootInfo)
                rootInfo.recycle()
                returnInfo2
            } else {
                val listIdText: List<AccessibilityNodeInfo> =
                    (tfs[idTextIndex] as AbstractTF.IdTextTF).findAll(rootInfo) as List<AccessibilityNodeInfo>
                if (Utils.isEmptyArray(listIdText)) {
                    return null
                }
                var returnInfo3: AccessibilityNodeInfo? = null
                for (info in listIdText) { //遍历找到匹配的
                    if (returnInfo3 == null) {
                        var isOk = true
                        for (tf in tfs) {
                            if (!tf.checkOk(info)) {
                                isOk = false
                                break
                            }
                        }
                        if (isOk) {
                            returnInfo3 = info
                        } else {
                            info.recycle()
                        }
                    } else {
                        info.recycle()
                    }
                }
                rootInfo.recycle()
                returnInfo3
            }
            else -> throw RuntimeException("由于时间有限，并且多了也没什么用，所以IdTF和TextTF只能有一个")
        }
    }

    /**
     * @param tfs 由于是递归循环，会忽略IdTF和TextTF
     */
    fun findFirstRecursive(
        parent: AccessibilityNodeInfo?,
        vararg tfs: AbstractTF<Any>
    ): AccessibilityNodeInfo? {
        if (parent == null) return null
        if (tfs.size == 0) throw InvalidParameterException("AbstractTF不允许传空")
        for (i in 0 until parent.childCount) {
            val child = parent.getChild(i) ?: continue
            var isOk = true
            for (tf in tfs) {
                if (!tf.checkOk(child)) {
                    isOk = false
                    break
                }
            }
            if (isOk) {
                return child
            } else {
                val childChild = findFirstRecursive(child, *tfs)
                child.recycle()
                if (childChild != null) {
                    return childChild
                }
            }
        }
        return null
    }

    companion object {
        var mService: CheckUrlService? = null
        // 公共方法
        /**
         * 辅助功能是否启动
         */
        val isStart: Boolean
            get() = mService != null
    }
}