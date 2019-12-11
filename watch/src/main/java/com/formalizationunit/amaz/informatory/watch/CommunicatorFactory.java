package com.formalizationunit.amaz.informatory.watch;

import android.content.Context;

import com.formalizationunit.amaz.informatory.common.communicators.LocalCommunicator;
import com.formalizationunit.amaz.informatory.common.models.CommunicatorClient;
import com.formalizationunit.amaz.informatory.common.util.Device;
import com.formalizationunit.amaz.informatory.watch.communicators.CommunicatorStandalone;
import com.formalizationunit.amaz.informatory.watch.communicators.interprocess.CommunicatorWidgetSide;

class CommunicatorFactory {
    static CommunicatorClient create(Context context) {
        if (BuildConfig.DEBUG) {
            if (!Device.isWatch()) {
                // This is host side.
                return new LocalCommunicator(context);
            }
        }
        if (BuildConfig.STAND_ALONE) {
            return new CommunicatorStandalone(context);
        }
        return new CommunicatorWidgetSide(context);
    }
}
