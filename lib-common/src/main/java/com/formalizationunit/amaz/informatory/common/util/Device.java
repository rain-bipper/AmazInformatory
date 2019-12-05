package com.formalizationunit.amaz.informatory.common.util;

import android.os.Build;

public class Device {
    public static boolean isWatch() {
        return "watch".equals(Build.DEVICE);
    }
}
