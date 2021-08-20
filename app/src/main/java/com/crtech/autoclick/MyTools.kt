package com.crtech.autoclick

import android.content.Context
import android.widget.Toast

 fun Context.toast(str: String) {
    Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
}

//集合是否是空的
fun isEmptyArray(list: Collection<*>?): Boolean {
    return list == null || list.isEmpty()
}

fun <T> isEmptyArray(list: Array<T>?): Boolean {
    return list == null || list.isEmpty()
}