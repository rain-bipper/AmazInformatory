package com.formalizationunit.amaz.informatory.transport;

import android.content.Context;

import androidx.annotation.Nullable;

import com.huami.watch.transport.TransportDataItem;

import java.util.HashMap;
import java.util.Map;

public class TransportDispatcher {
    private final Transport mTransport;
    private final Map<String, ActionHandler> mActionHandlers = new HashMap<>();

    public interface ActionHandler {
        void onAction(@Nullable String data);
    }

    public TransportDispatcher(Context context, String channel) {
        mTransport = new Transport(context.getApplicationContext(), channel,
                this::onDataReceived);
    }

    public void destroy() {
        mTransport.destroy();
    }

    public void registerActionHandler(String action, ActionHandler handler) {
        mActionHandlers.put(action, handler);
    }

    public void send(String action, @Nullable String data, Runnable callback) {
        mTransport.send(action, Message.toData(data), callback);
    }

    private void onDataReceived(TransportDataItem data) {
        ActionHandler handler = mActionHandlers.get(data.getAction());

        if (handler != null) {
            handler.onAction(Message.fromData(data.getData()));
        }
    }
}
