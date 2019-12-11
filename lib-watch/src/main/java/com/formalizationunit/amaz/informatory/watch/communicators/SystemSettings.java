package com.formalizationunit.amaz.informatory.watch.communicators;

import android.content.Context;

import androidx.annotation.Nullable;

public class SystemSettings {
    public static final String WEATHER_KEY = "WeatherInfo";

    @Nullable
    public static String get(Context context, String key) {
        return android.provider.Settings.System.getString(context.getApplicationContext()
                .getContentResolver(), key);
    }

    public static boolean set(Context context, String key, String data) {
        return android.provider.Settings.System.putString(context.getApplicationContext()
                .getContentResolver(), key, data);
    }
}
