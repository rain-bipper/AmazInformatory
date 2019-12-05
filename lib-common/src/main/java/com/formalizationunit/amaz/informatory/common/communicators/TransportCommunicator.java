package com.formalizationunit.amaz.informatory.common.communicators;

import android.content.Context;

import com.formalizationunit.amaz.informatory.common.models.CommunicatorModel;
import com.formalizationunit.amaz.informatory.transport.Actions;
import com.formalizationunit.amaz.informatory.transport.Channels;
import com.formalizationunit.amaz.informatory.transport.TransportDispatcher;

class TransportCommunicator implements CommunicatorModel {
    private final TransportDispatcher mTransportDispatcher;

    public TransportCommunicator(Context context) {
        mTransportDispatcher = new TransportDispatcher(context, Channels.MAIN_CHANNEL);
    }

    @Override
    public void destroy() {
        mTransportDispatcher.destroy();
    }

    @Override
    public void registerSentWeatherHandler(final ActionHandler handler) {
        mTransportDispatcher.registerActionHandler(Actions.SEND_WEATHER, handler::onAction);
    }

    @Override
    public void registerRequestedWeatherHandler(final ActionHandler handler) {
        mTransportDispatcher.registerActionHandler(Actions.REQUEST_WEATHER, handler::onAction);
    }

    @Override
    public void sendWeather(String jsonData, Runnable callback) {
        mTransportDispatcher.send(Actions.SEND_WEATHER, jsonData, callback);
    }

    @Override
    public void requestWeather(Runnable callback) {
        mTransportDispatcher.send(Actions.REQUEST_WEATHER, null, callback);
    }
}
