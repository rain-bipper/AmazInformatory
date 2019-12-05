package com.formalizationunit.amaz.informatory.common.communicators;

import android.content.Context;

import com.formalizationunit.amaz.informatory.common.BuildConfig;
import com.formalizationunit.amaz.informatory.common.models.CommunicatorModel;
import com.formalizationunit.amaz.informatory.common.util.Device;

public class CommunicatorFactory {
    private static boolean sPreferLocalConnection;

    public static CommunicatorModel create(Context context) {
        if (BuildConfig.DEBUG) {
            if (!Device.isWatch()) {
                // This is host side.
                if (sPreferLocalConnection) {
                    return new LocalCommunicator(context);
                }
            }
        }
        //return new LocalCommunicator(context);
        return new TransportCommunicator(context);
    }

    public static void setPreferLocalConnectionOnHost(boolean preferLocalConnection) {
        if (!BuildConfig.DEBUG) {
            throw new IllegalStateException("Debug method called in release version.");
        }
        sPreferLocalConnection = preferLocalConnection;
    }
}
