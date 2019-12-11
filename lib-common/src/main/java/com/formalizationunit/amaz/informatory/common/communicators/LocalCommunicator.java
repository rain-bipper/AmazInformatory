package com.formalizationunit.amaz.informatory.common.communicators;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.Nullable;

import com.formalizationunit.amaz.informatory.common.logger.Logger;
import com.formalizationunit.amaz.informatory.common.models.CommunicatorActionHandler;
import com.formalizationunit.amaz.informatory.common.models.CommunicatorClient;
import com.formalizationunit.amaz.informatory.common.models.CommunicatorHost;

import java.util.ArrayList;
import java.util.List;

public class LocalCommunicator implements CommunicatorHost, CommunicatorClient {
    private static final String TAG = "<LocalCommunicator>";
    static final String DATA_KEY = "DATA_KEY";

    private final Context mContext;
    private final List<BroadcastReceiver> mHandlers = new ArrayList<>();

    public LocalCommunicator(Context context) {
        mContext = context;
    }

    @Override
    public void registerSentWeatherHandler(final CommunicatorActionHandler handler) {
        registerReceiver(TransportActions.SEND_WEATHER, handler);
    }

    @Override
    public void requestWeather(Runnable callback) {
        Logger.log(TAG, "LocalCommunicatorClient.requestWeather");
        send(TransportActions.REQUEST_WEATHER, null, callback);
    }

    @Override
    public void registerRequestedWeatherHandler(CommunicatorActionHandler handler) {
        registerReceiver(TransportActions.REQUEST_WEATHER, handler);
    }

    @Override
    public void sendWeather(String jsonData, Runnable callback) {
        Logger.log(TAG, "LocalCommunicatorHost.sendWeather");
        send(TransportActions.SEND_WEATHER, jsonData, callback);
    }

    @Override
    public void destroy() {
        for (BroadcastReceiver handler : mHandlers) {
            mContext.unregisterReceiver(handler);
        }
        mHandlers.clear();
    }

    public void send(String action, @Nullable String data, Runnable callback) {
        Intent intent = new Intent(action);
        if (data != null) {
            intent.putExtra(DATA_KEY, data);
        }
        mContext.sendBroadcast(intent);

        // Call back immediately. Use sendOrderedBroadcast with resultReceiver, to call back after
        // all receivers are done.
        callback.run();
    }

    public void registerReceiver(String action, CommunicatorActionHandler handler) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (action.equals(intent.getAction())) {
                    handler.onAction(intent.getStringExtra(DATA_KEY));
                }
            }
        };
        mContext.registerReceiver(receiver,
                new IntentFilter(this.getClass().getName() + "/" + action));
        mHandlers.add(receiver);
    }
}
