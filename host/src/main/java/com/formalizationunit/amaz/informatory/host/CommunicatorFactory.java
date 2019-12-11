package com.formalizationunit.amaz.informatory.host;

import android.content.Context;

import com.formalizationunit.amaz.informatory.common.communicators.LocalCommunicator;
import com.formalizationunit.amaz.informatory.common.communicators.RemoteCommunicatorHost;
import com.formalizationunit.amaz.informatory.common.models.CommunicatorHost;

class CommunicatorFactory {
    static CommunicatorHost create(Context context) {
        if (BuildConfig.DEBUG && BuildConfig.LOCAL_CONNECTION) {
            return new LocalCommunicator(context);
        }
        return new RemoteCommunicatorHost(context);
    }
}
