package com.crtech.autoclick;

import android.widget.Toast;

import java.util.Collection;

public class Utils {

    //集合是否是空的
    public static boolean isEmptyArray(Collection list) {
        return list == null || list.size() == 0;
    }

    public static <T> boolean isEmptyArray(T[] list) {
        return list == null || list.length == 0;
    }
}
