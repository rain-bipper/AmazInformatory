package com.formalizationunit.amaz.informatory.transport.logger;

import androidx.annotation.Nullable;

public class Logger {
    @Nullable
    private static LoggerInterface mLogger;

    public static void setLogger(LoggerInterface logger) {
        mLogger = logger;
    }

    static public void log(String tag, String data) {
        if (mLogger != null) {
            mLogger.log(tag, data);
        }
    }
}
