package com.formalizationunit.amaz.informatory.common.models;

public interface CommunicatorClient {
    void registerSentWeatherHandler(CommunicatorActionHandler handler);
    void requestWeather(Runnable callback);

    void destroy();
}
