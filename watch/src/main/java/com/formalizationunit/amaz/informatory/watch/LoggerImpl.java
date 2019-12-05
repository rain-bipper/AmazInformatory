package com.formalizationunit.amaz.informatory.watch;

import com.formalizationunit.amaz.informatory.common.logger.LoggerInterface;

public class LoggerImpl implements LoggerInterface {
    @Override
    public void log(String tag, String data) {
        android.util.Log.e(tag, data);
    }

    @Override
    public void destroy() {
        // Empty be design.
    }
}
