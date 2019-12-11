package com.formalizationunit.amaz.informatory.watch.communicators;

import android.content.Context;

import com.formalizationunit.amaz.informatory.common.communicators.RemoteCommunicatorClient;
import com.formalizationunit.amaz.informatory.watch.communicators.SystemSettings;

public class CommunicatorStandalone extends RemoteCommunicatorClient {
    public CommunicatorStandalone(Context context) {
        super(context);
        registerSentWeatherHandler(data -> {
            SystemSettings.set(context, "WeatherInfo", data);
            SystemSettings.set(context, "WeatherCheckedSummary", data);
        });
    }
}
