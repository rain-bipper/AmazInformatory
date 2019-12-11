package com.formalizationunit.amaz.informatory.watch;

import android.content.Context;

import com.formalizationunit.amaz.informatory.common.models.CommunicatorClient;

public class CommunicatorHolder {
    private static CommunicatorClient mCommunicator;

    public static void ensureCreated(Context context) {
        mCommunicator = CommunicatorFactory.create(context);
    }

    public static CommunicatorClient getCommunicator() {
        return mCommunicator;
    }
}
