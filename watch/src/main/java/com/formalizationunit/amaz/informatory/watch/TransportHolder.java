package com.formalizationunit.amaz.informatory.watch;

import android.content.Context;

import com.formalizationunit.amaz.informatory.common.communicators.CommunicatorFactory;
import com.formalizationunit.amaz.informatory.common.models.CommunicatorModel;

public class TransportHolder {
    private static CommunicatorModel mCommunicator;

    public static void ensureCreated(Context context) {
        mCommunicator = CommunicatorFactory.create(context);
    }

    public static CommunicatorModel getCommunicator() {
        return mCommunicator;
    }
}
