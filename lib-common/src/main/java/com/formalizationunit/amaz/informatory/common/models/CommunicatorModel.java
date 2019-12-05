package com.formalizationunit.amaz.informatory.common.models;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

public interface CommunicatorModel {
    interface ActionHandler {
        @WorkerThread
        void onAction(@Nullable String data);
    }

    void destroy();

    void registerSentWeatherHandler(ActionHandler handler);
    void registerRequestedWeatherHandler(ActionHandler handler);

    void sendWeather(String weatherJson, Runnable callback);
    void requestWeather(Runnable callback);
}
