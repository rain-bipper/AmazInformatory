package com.formalizationunit.amaz.informatory.host;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.formalizationunit.amaz.informatory.common.logger.Logger;
import com.formalizationunit.amaz.informatory.host.weather.ApiKeyProvider;
import com.formalizationunit.amaz.informatory.host.weather.WeatherController;
import com.formalizationunit.amaz.informatory.common.models.CommunicatorHost;
import com.formalizationunit.amaz.informatory.common.util.PeriodicProcessor;

class Facade {
    private static final String TAG = "<Facade>";
    private static final int PERIOD_MS = 15 * 60 * 1000;  // 15 minutes.

    private final WeatherController mWeatherController;
    private final CommunicatorHost mCommunicator;
    private final Callback mPeriodicCallback;
    private PeriodicProcessor mPeriodicProcessor;

    interface Callback {
        void onDone();
        void onError(String reason);
    }


    Facade(Context context, CommunicatorHost communicator, ApiKeyProvider apiKeyProvider,
           Callback periodicCallback) {
        mCommunicator = communicator;
        mCommunicator.registerRequestedWeatherHandler((data) -> updateRequestedFromWatch());
        mPeriodicCallback = periodicCallback;
        mWeatherController = new WeatherController(context, mCommunicator, apiKeyProvider);

        Handler handler = new Handler(Looper.getMainLooper());
        mPeriodicProcessor = new PeriodicProcessor(handler::post, () -> {
            Logger.log(TAG, "auto process");
            process(mPeriodicCallback);
        }, PERIOD_MS);
    }

    void process(Callback callback) {
        mWeatherController.run(new WeatherController.Callback() {
            @Override
            public void onDone() {
                callback.onDone();
            }

            @Override
            public void onError(String reason) {
                callback.onError(reason);
            }
        });
    }

    void destroy() {
        mPeriodicProcessor.destroy();
        mCommunicator.destroy();
    }

    private void updateRequestedFromWatch() {
        Logger.log(TAG, "Update requested from watch");

        process(mPeriodicCallback);
    }
}
