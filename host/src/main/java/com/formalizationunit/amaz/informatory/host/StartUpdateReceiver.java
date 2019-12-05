package com.formalizationunit.amaz.informatory.host;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //UpdateService.schedule(context, 30 * 60 * 1000);
        MainService.start(context);
    }
}
