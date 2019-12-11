package com.formalizationunit.amaz.informatory.watch.communicators.interprocess;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;

import com.formalizationunit.amaz.informatory.common.communicators.LocalCommunicator;
import com.formalizationunit.amaz.informatory.common.communicators.TransportActions;
import com.formalizationunit.amaz.informatory.watch.communicators.SystemSettings;
import com.formalizationunit.amaz.informatory.common.logger.Logger;
import com.formalizationunit.amaz.informatory.common.models.CommunicatorActionHandler;
import com.formalizationunit.amaz.informatory.common.models.CommunicatorClient;

public class CommunicatorWidgetSide implements CommunicatorClient {
    private static final String TAG = "<CommunicatorWidgetSide>";

    private final LocalCommunicator mOut;
    private final Context mContext;

    public CommunicatorWidgetSide(Context context) {
        mContext = context;
        mOut = new LocalCommunicator(context);
    }

    @Override
    public void registerSentWeatherHandler(final CommunicatorActionHandler handler) {
        mContext.getApplicationContext().getContentResolver().registerContentObserver(
                Settings.System.getUriFor(SystemSettings.WEATHER_KEY), false,
                new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                String data = SystemSettings.get(mContext, SystemSettings.WEATHER_KEY);
                if (data != null) {
                    handler.onAction(data);
                }
            }
        });
    }

    @Override
    public void requestWeather(Runnable callback) {
        Logger.log(TAG, "CommunicatorWidgetSide.requestWeather");
        mOut.send(TransportActions.REQUEST_WEATHER, null, callback);
    }

    @Override
    public void destroy() {
        mOut.destroy();
    }
}
