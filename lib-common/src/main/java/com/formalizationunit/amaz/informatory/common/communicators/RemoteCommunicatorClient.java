package com.formalizationunit.amaz.informatory.common.communicators;

import android.content.Context;

import com.formalizationunit.amaz.informatory.common.models.CommunicatorActionHandler;
import com.formalizationunit.amaz.informatory.common.models.CommunicatorClient;
import com.formalizationunit.amaz.informatory.transport.TransportDispatcher;

public class RemoteCommunicatorClient implements CommunicatorClient {
    final TransportDispatcher mTransportDispatcher;

    public RemoteCommunicatorClient(Context context) {
        mTransportDispatcher = new TransportDispatcher(context, TransportChannels.MAIN_CHANNEL);
    }

    @Override
    public void registerSentWeatherHandler(final CommunicatorActionHandler handler) {
        mTransportDispatcher.registerActionHandler(TransportActions.SEND_WEATHER, handler::onAction);
    }

    @Override
    public void requestWeather(Runnable callback) {
        mTransportDispatcher.send(TransportActions.REQUEST_WEATHER, null, callback);
    }

    @Override
    public void destroy() {
        mTransportDispatcher.destroy();
    }
}
