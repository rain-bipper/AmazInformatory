package com.formalizationunit.amaz.informatory.watch.system;

import android.content.Context;

import androidx.annotation.Nullable;

public class SystemSettings {

    @Nullable
    static public String get(Context context, String key) {
        return android.provider.Settings.System.getString(context.getApplicationContext()
                .getContentResolver(), key);
    }

    static public boolean set(Context context, String key, String data) {
        return android.provider.Settings.System.putString(context.getApplicationContext()
                .getContentResolver(), key, data);
    }
}
