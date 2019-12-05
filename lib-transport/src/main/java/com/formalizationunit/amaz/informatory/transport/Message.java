package com.formalizationunit.amaz.informatory.transport;

import androidx.annotation.Nullable;

import com.huami.watch.transport.DataBundle;

class Message {
    private static final String DATA_KEY = "DATA_KEY";

    static DataBundle toData(@Nullable String obj) {
        DataBundle data = new DataBundle();
        data.putString(DATA_KEY, obj);
        return data;
    }

    @Nullable
    static String fromData(DataBundle data) {
        try {
            return data.getString(DATA_KEY);
        } catch (ClassCastException e) {
            return null;
        }
    }
}
