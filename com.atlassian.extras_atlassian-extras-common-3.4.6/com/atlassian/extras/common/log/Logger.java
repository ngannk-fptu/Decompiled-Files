/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.extras.common.log;

import com.atlassian.extras.common.log.StdErrLogger;

public class Logger {
    private static final Class<?> LOG4J_LOGGER_CLASS;
    private static Log logger;
    private static Level stdErrLogLevel;

    public static void setInstance(Log logger) {
        Logger.logger = logger;
    }

    public static Log getInstance(Class clazz) {
        if (logger != null) {
            return logger;
        }
        try {
            if (LOG4J_LOGGER_CLASS != null) {
                Log log4j = (Log)LOG4J_LOGGER_CLASS.newInstance();
                log4j.setClass(clazz);
                return log4j;
            }
        }
        catch (IllegalAccessException illegalAccessException) {
        }
        catch (InstantiationException instantiationException) {
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
            // empty catch block
        }
        logger = new StdErrLogger(stdErrLogLevel);
        return logger;
    }

    public static void setStdErrLogLevel(Level stdErrLogLevel) {
        if (stdErrLogLevel == null) {
            throw new IllegalArgumentException("StdErrLogger Log Level must not be null.");
        }
        Logger.stdErrLogLevel = stdErrLogLevel;
        logger = null;
    }

    static {
        Class<?> log4jLogger = null;
        try {
            log4jLogger = Class.forName("com.atlassian.extras.common.log.Log4jLogger");
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        finally {
            LOG4J_LOGGER_CLASS = log4jLogger;
        }
        stdErrLogLevel = Level.INFO;
    }

    public static interface Log {
        public void setClass(Class var1);

        public void debug(Object var1);

        public void debug(Object var1, Throwable var2);

        public void info(Object var1);

        public void info(Object var1, Throwable var2);

        public void warn(Object var1);

        public void warn(Object var1, Throwable var2);

        public void error(Object var1);

        public void error(Object var1, Throwable var2);

        public void fatal(Object var1);

        public void fatal(Object var1, Throwable var2);
    }

    public static enum Level {
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL;

    }
}

