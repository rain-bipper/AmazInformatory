package com.formalizationunit.amaz.informatory.watch.communicators.interprocess;

import android.content.Context;

import com.formalizationunit.amaz.informatory.common.communicators.LocalCommunicator;
import com.formalizationunit.amaz.informatory.common.communicators.TransportActions;
import com.formalizationunit.amaz.informatory.watch.communicators.SystemSettings;
import com.formalizationunit.amaz.informatory.common.models.CommunicatorActionHandler;
import com.formalizationunit.amaz.informatory.common.models.CommunicatorHost;

public class CommunicatorServiceSide implements CommunicatorHost {

    private final LocalCommunicator mIn;
    private final Context mContext;

    public CommunicatorServiceSide(Context context) {
        mIn = new LocalCommunicator(context);
        mContext = context;
    }

    @Override
    public void registerRequestedWeatherHandler(CommunicatorActionHandler handler) {
        mIn.registerReceiver(TransportActions.REQUEST_WEATHER, handler);
    }

    @Override
    public void sendWeather(String weatherJson, Runnable callback) {
        SystemSettings.set(mContext, "WeatherInfo", weatherJson);
        SystemSettings.set(mContext, "WeatherCheckedSummary", weatherJson);
    }

    @Override
    public void destroy() {
        mIn.destroy();
    }
}
