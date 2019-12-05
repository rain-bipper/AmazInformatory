package com.formalizationunit.amaz.informatory.common.communicators;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.formalizationunit.amaz.informatory.common.logger.Logger;
import com.formalizationunit.amaz.informatory.common.models.CommunicatorModel;
import com.formalizationunit.amaz.informatory.transport.Actions;

import java.util.ArrayList;
import java.util.List;

class LocalCommunicator implements CommunicatorModel {
    private static final String TAG = "<LocalCommunicator>";
    private static final String DATA_KEY = "DATA_KEY";

    private final Context mContext;
    private final List<BroadcastReceiver> mHandlers = new ArrayList<>();

    public LocalCommunicator(Context context) {
        mContext = context;
    }

    @Override
    public void destroy() {
        for (BroadcastReceiver handler : mHandlers) {
            mContext.unregisterReceiver(handler);
        }
        mHandlers.clear();
    }

    @Override
    public void registerSentWeatherHandler(final ActionHandler handler) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Actions.SEND_WEATHER.equals(intent.getAction())) {
                    handler.onAction(intent.getStringExtra(DATA_KEY));
                }
            }
        };
        mContext.registerReceiver(receiver, new IntentFilter(Actions.SEND_WEATHER));
    }

    @Override
    public void registerRequestedWeatherHandler(ActionHandler handler) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Actions.REQUEST_WEATHER.equals(intent.getAction())) {
                    handler.onAction(intent.getStringExtra(DATA_KEY));
                }
            }
        };
        mContext.registerReceiver(receiver, new IntentFilter(Actions.REQUEST_WEATHER));
    }

    @Override
    public void sendWeather(String jsonData, Runnable callback) {
        Logger.log(TAG, "LocalCommunicator.sendWeather " + jsonData);

        Intent intent = new Intent(Actions.SEND_WEATHER);
        intent.putExtra(DATA_KEY, jsonData);
        mContext.sendBroadcast(intent);

        new Handler().postDelayed(callback, 10000);
    }

    @Override
    public void requestWeather(Runnable callback) {
        Logger.log(TAG, "LocalCommunicator.requestWeather");

        Intent intent = new Intent(Actions.REQUEST_WEATHER);
        mContext.sendBroadcast(intent);

        new Handler().postDelayed(callback, 10000);
    }
}
