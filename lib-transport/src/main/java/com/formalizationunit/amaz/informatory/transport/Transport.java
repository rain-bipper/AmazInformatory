package com.formalizationunit.amaz.informatory.transport;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.formalizationunit.amaz.informatory.transport.logger.Logger;
import com.huami.watch.transport.DataBundle;
import com.huami.watch.transport.TransportDataItem;
import com.huami.watch.transport.Transporter;
import com.huami.watch.transport.TransporterClassic;

import java.util.ArrayDeque;
import java.util.Queue;

class Transport implements Transporter.ChannelListener, Transporter.DataListener, Transporter.ServiceConnectionListener {
    private static final String TAG = "<Transport>";
    private final Transporter mTransporter;
    private final Queue<Request> mPendingRequests = new ArrayDeque<>();
    @Nullable
    private final Transporter.DataListener mDataListener;

    Transport(Context context, String channel, @Nullable Transporter.DataListener dataListener) {
        Logger.log(TAG, "begin");
        mTransporter = TransporterClassic.get(context, channel);
        mTransporter.addDataListener(this);
        mTransporter.addServiceConnectionListener(this);
        mTransporter.addChannelListener(this);

        mDataListener = dataListener;
        if (dataListener != null) {
            mTransporter.addDataListener(dataListener);
        }
        Logger.log(TAG, "connecting begin. Available " + mTransporter.isAvailable() + ", connected " + mTransporter.isTransportServiceConnected());
        mTransporter.connectTransportService();
        Logger.log(TAG, "connecting end. Available " + mTransporter.isAvailable() + ", connected " + mTransporter.isTransportServiceConnected());
    }

    void destroy() {
        Logger.log(TAG, "destroy");
        if (mTransporter.isTransportServiceConnected()) {
            mTransporter.disconnectTransportService();
        }
        mTransporter.removeDataListener(this);
        mTransporter.removeServiceConnectionListener(this);
        mTransporter.removeChannelListener(this);

        if (mDataListener != null) {
            mTransporter.removeDataListener(mDataListener);
        }

        mPendingRequests.clear();
    }

    void send(String action, DataBundle dataBundle, Runnable callback) {
        if (!mTransporter.isTransportServiceConnected()) {
            Logger.log(TAG, "MainService Transport Service Not Connected");
            mPendingRequests.add(new Request(action, dataBundle, callback));
            mTransporter.connectTransportService();
            return;
        }

        mTransporter.send(action, dataBundle, dataTransportResult -> {
            Logger.log(TAG, "Send result: " + dataTransportResult.toString());
            processQueue();
            callback.run();
        });
    }

    private void processQueue() {
        Request request = mPendingRequests.poll();

        if (request == null) {
            Logger.log(TAG, "processQueue no pending");
            return;
        }

        Logger.log(TAG, "processQueue, left " + mPendingRequests.size());

        send(request.action, request.dataBundle, request.callback);
    }

    @Override
    public void onDataReceived(TransportDataItem transportDataItem) {
        Logger.log(TAG, "onDataReceived: " + transportDataItem.toString());
    }

    @Override
    public void onServiceConnected(Bundle bundle) {
        Logger.log(TAG, "onServiceConnected " + bundle);
    }

    @Override
    public void onServiceConnectionFailed(Transporter.ConnectionResult connectionResult) {
        Logger.log(TAG, "onServiceConnectionFailed " + connectionResult);
    }

    @Override
    public void onServiceDisconnected(Transporter.ConnectionResult connectionResult) {
        Logger.log(TAG, "onServiceDisconnected " + connectionResult);
    }

    @Override
    public void onChannelChanged(boolean b) {
        Logger.log(TAG, "onChannelChanged b: " + b);

        if (b) {
            processQueue();
        }
    }

    private class Request {
        private final String action;
        private final DataBundle dataBundle;
        private final Runnable callback;

        private Request(String action, DataBundle dataBundle, Runnable callback) {
            this.action = action;
            this.dataBundle = dataBundle;
            this.callback = callback;
        }
    }
}
