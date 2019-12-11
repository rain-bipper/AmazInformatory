package com.formalizationunit.amaz.informatory.common.models;

public interface CommunicatorHost {
    void registerRequestedWeatherHandler(CommunicatorActionHandler handler);
    void sendWeather(String weatherJson, Runnable callback);

    void destroy();
}
