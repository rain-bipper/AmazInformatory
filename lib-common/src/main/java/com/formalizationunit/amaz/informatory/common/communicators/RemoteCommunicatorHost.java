package com.formalizationunit.amaz.informatory.common.communicators;

import android.content.Context;

import com.formalizationunit.amaz.informatory.common.models.CommunicatorActionHandler;
import com.formalizationunit.amaz.informatory.common.models.CommunicatorHost;
import com.formalizationunit.amaz.informatory.transport.TransportDispatcher;

public class RemoteCommunicatorHost implements CommunicatorHost {
    final TransportDispatcher mTransportDispatcher;

    public RemoteCommunicatorHost(Context context) {
        mTransportDispatcher = new TransportDispatcher(context, TransportChannels.MAIN_CHANNEL);
    }

    @Override
    public void registerRequestedWeatherHandler(final CommunicatorActionHandler handler) {
        mTransportDispatcher.registerActionHandler(TransportActions.REQUEST_WEATHER, handler::onAction);
    }

    @Override
    public void sendWeather(String jsonData, Runnable callback) {
        mTransportDispatcher.send(TransportActions.SEND_WEATHER, jsonData, callback);
    }

    @Override
    public void destroy() {
        mTransportDispatcher.destroy();
    }
}
