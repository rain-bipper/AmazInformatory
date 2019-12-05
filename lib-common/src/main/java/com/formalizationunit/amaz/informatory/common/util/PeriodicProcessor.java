package com.formalizationunit.amaz.informatory.common.util;

import java.util.Timer;
import java.util.TimerTask;

public class PeriodicProcessor {
    private final UiRunner mUiRunner;
    private final Runnable mRunnable;
    private final Timer mTimer = new Timer();
    private final TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            mUiRunner.runOnUi(mRunnable);
        }
    };

    public PeriodicProcessor(UiRunner uiRunner, Runnable runnable, int periodMs) {
        mUiRunner = uiRunner;
        mRunnable = runnable;
        mTimer.schedule(mTimerTask, 0, periodMs);
    }

    public void destroy() {
        mTimerTask.cancel();
        mTimer.cancel();
    }
}
