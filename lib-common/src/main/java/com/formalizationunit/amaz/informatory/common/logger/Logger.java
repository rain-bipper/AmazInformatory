package com.formalizationunit.amaz.informatory.common.logger;

public class Logger {
    private static LoggerInterface mLogger;

    public static void setLogger(LoggerInterface logger) {
        mLogger = logger;
        com.formalizationunit.amaz.informatory.transport.logger.Logger.setLogger(Logger::log);
    }

    static public void log(String tag, String data) {
        mLogger.log(tag, data);
    }

    static public void destroy() {
        mLogger.destroy();
    }

    static public boolean inited() {
        return mLogger != null;
    }
}
