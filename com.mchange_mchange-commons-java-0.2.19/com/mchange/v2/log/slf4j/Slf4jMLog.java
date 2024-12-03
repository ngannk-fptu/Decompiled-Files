/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.ILoggerFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.mchange.v2.log.slf4j;

import com.mchange.v2.log.FallbackMLog;
import com.mchange.v2.log.LogUtils;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogInitializationException;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.log.NullMLogger;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Slf4jMLog
extends MLog {
    static final Object[] EMPTY_OBJ_ARRAY = new Object[0];
    private static final int ALL_INTVAL = MLevel.ALL.intValue();
    private static final int CONFIG_INTVAL = MLevel.CONFIG.intValue();
    private static final int FINE_INTVAL = MLevel.FINE.intValue();
    private static final int FINER_INTVAL = MLevel.FINER.intValue();
    private static final int FINEST_INTVAL = MLevel.FINEST.intValue();
    private static final int INFO_INTVAL = MLevel.INFO.intValue();
    private static final int OFF_INTVAL = MLevel.OFF.intValue();
    private static final int SEVERE_INTVAL = MLevel.SEVERE.intValue();
    private static final int WARNING_INTVAL = MLevel.WARNING.intValue();
    static final String CHECK_CLASS = "org.slf4j.LoggerFactory";
    static final String DFLT_LOGGER_NAME = "global";

    public Slf4jMLog() throws ClassNotFoundException, MLogInitializationException {
        Class.forName(CHECK_CLASS);
        ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();
        if (iLoggerFactory == null || iLoggerFactory.getClass().getName() == "org.slf4j.helpers.NOPLoggerFactory") {
            throw new MLogInitializationException("slf4j found no binding or threatened to use its (dangerously silent) NOPLogger. We consider the slf4j library not found.");
        }
    }

    @Override
    public MLogger getMLogger(String string) {
        Logger logger = LoggerFactory.getLogger((String)string);
        if (logger == null) {
            this.fallbackWarn(" with name '" + string + "'");
            return NullMLogger.instance();
        }
        return new Slf4jMLogger(logger);
    }

    @Override
    public MLogger getMLogger() {
        Logger logger = LoggerFactory.getLogger((String)DFLT_LOGGER_NAME);
        if (logger == null) {
            this.fallbackWarn(" (default, with name 'global')");
            return NullMLogger.instance();
        }
        return new Slf4jMLogger(logger);
    }

    private void fallbackWarn(String string) {
        FallbackMLog.getLogger().warning("Could not create or find slf4j Logger" + string + ". Using NullMLogger. All messages sent to thislogger will be silently ignored. You might want to fix this.");
    }

    private static final class Slf4jMLogger
    implements MLogger {
        static final String FQCN = Slf4jMLogger.class.getName();
        final Logger logger;
        final LevelLogger traceL;
        final LevelLogger debugL;
        final LevelLogger infoL;
        final LevelLogger warnL;
        final LevelLogger errorL;
        final LevelLogger offL;
        MLevel myLevel = null;

        Slf4jMLogger(Logger logger) {
            this.logger = logger;
            this.traceL = new TraceLogger();
            this.debugL = new DebugLogger();
            this.infoL = new InfoLogger();
            this.warnL = new WarnLogger();
            this.errorL = new ErrorLogger();
            this.offL = new OffLogger();
        }

        private MLevel guessMLevel() {
            if (this.logger.isTraceEnabled()) {
                return MLevel.FINEST;
            }
            if (this.logger.isDebugEnabled()) {
                return MLevel.FINER;
            }
            if (this.logger.isInfoEnabled()) {
                return MLevel.INFO;
            }
            if (this.logger.isWarnEnabled()) {
                return MLevel.WARNING;
            }
            if (this.logger.isErrorEnabled()) {
                return MLevel.SEVERE;
            }
            return MLevel.OFF;
        }

        private synchronized boolean myLevelMayBeLoggable(int n) {
            return this.myLevel == null || n >= this.myLevel.intValue();
        }

        private LevelLogger levelLogger(MLevel mLevel) {
            LevelLogger levelLogger = this.offL;
            int n = mLevel.intValue();
            if (this.myLevelMayBeLoggable(n) && n >= FINEST_INTVAL) {
                if (n < FINER_INTVAL) {
                    if (this.logger.isTraceEnabled()) {
                        levelLogger = this.traceL;
                    }
                } else if (n < INFO_INTVAL) {
                    if (this.logger.isDebugEnabled()) {
                        levelLogger = this.debugL;
                    }
                } else if (n < WARNING_INTVAL) {
                    if (this.logger.isInfoEnabled()) {
                        levelLogger = this.infoL;
                    }
                } else if (n < SEVERE_INTVAL) {
                    if (this.logger.isWarnEnabled()) {
                        levelLogger = this.warnL;
                    }
                } else if (n < OFF_INTVAL && this.logger.isErrorEnabled()) {
                    levelLogger = this.errorL;
                }
            }
            return levelLogger;
        }

        @Override
        public ResourceBundle getResourceBundle() {
            return null;
        }

        @Override
        public String getResourceBundleName() {
            return null;
        }

        @Override
        public void setFilter(Object object) throws SecurityException {
            this.warning("setFilter() not supported by MLogger " + this.getClass().getName());
        }

        @Override
        public Object getFilter() {
            return null;
        }

        @Override
        public void log(MLevel mLevel, String string) {
            this.levelLogger(mLevel).log(string);
        }

        @Override
        public void log(MLevel mLevel, String string, Object object) {
            this.levelLogger(mLevel).log(string, object);
        }

        @Override
        public void log(MLevel mLevel, String string, Object[] objectArray) {
            this.levelLogger(mLevel).log(string, objectArray);
        }

        @Override
        public void log(MLevel mLevel, String string, Throwable throwable) {
            this.levelLogger(mLevel).log(string, throwable);
        }

        @Override
        public void logp(MLevel mLevel, String string, String string2, String string3) {
            this.levelLogger(mLevel).log(LogUtils.createMessage(string, string2, string3));
        }

        @Override
        public void logp(MLevel mLevel, String string, String string2, String string3, Object object) {
            this.levelLogger(mLevel).log(LogUtils.createMessage(string, string2, string3 != null ? MessageFormat.format(string3, object) : null));
        }

        @Override
        public void logp(MLevel mLevel, String string, String string2, String string3, Object[] objectArray) {
            this.levelLogger(mLevel).log(LogUtils.createMessage(string, string2, string3 != null ? MessageFormat.format(string3, objectArray) : null));
        }

        @Override
        public void logp(MLevel mLevel, String string, String string2, String string3, Throwable throwable) {
            this.levelLogger(mLevel).log(LogUtils.createMessage(string, string2, string3), throwable);
        }

        @Override
        public void logrb(MLevel mLevel, String string, String string2, String string3, String string4) {
            this.levelLogger(mLevel).log(LogUtils.createMessage(string, string2, LogUtils.formatMessage(string3, string4, null)));
        }

        @Override
        public void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Object object) {
            this.levelLogger(mLevel).log(LogUtils.createMessage(string, string2, LogUtils.formatMessage(string3, string4, new Object[]{object})));
        }

        @Override
        public void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Object[] objectArray) {
            this.levelLogger(mLevel).log(LogUtils.createMessage(string, string2, LogUtils.formatMessage(string3, string4, objectArray)));
        }

        @Override
        public void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Throwable throwable) {
            this.levelLogger(mLevel).log(LogUtils.createMessage(string, string2, LogUtils.formatMessage(string3, string4, null)), throwable);
        }

        @Override
        public void entering(String string, String string2) {
            this.traceL.log(LogUtils.createMessage(string, string2, "entering method."));
        }

        @Override
        public void entering(String string, String string2, Object object) {
            this.traceL.log(LogUtils.createMessage(string, string2, "entering method... param: " + object.toString()));
        }

        @Override
        public void entering(String string, String string2, Object[] objectArray) {
            this.traceL.log(LogUtils.createMessage(string, string2, "entering method... " + LogUtils.createParamsList(objectArray)));
        }

        @Override
        public void exiting(String string, String string2) {
            this.traceL.log(LogUtils.createMessage(string, string2, "exiting method."));
        }

        @Override
        public void exiting(String string, String string2, Object object) {
            this.traceL.log(LogUtils.createMessage(string, string2, "exiting method... result: " + object.toString()));
        }

        @Override
        public void throwing(String string, String string2, Throwable throwable) {
            this.traceL.log(LogUtils.createMessage(string, string2, "throwing exception... "), throwable);
        }

        @Override
        public void severe(String string) {
            this.errorL.log(string);
        }

        @Override
        public void warning(String string) {
            this.warnL.log(string);
        }

        @Override
        public void info(String string) {
            this.infoL.log(string);
        }

        @Override
        public void config(String string) {
            this.debugL.log(string);
        }

        @Override
        public void fine(String string) {
            this.debugL.log(string);
        }

        @Override
        public void finer(String string) {
            this.debugL.log(string);
        }

        @Override
        public void finest(String string) {
            this.traceL.log(string);
        }

        @Override
        public synchronized void setLevel(MLevel mLevel) throws SecurityException {
            this.myLevel = mLevel;
        }

        @Override
        public synchronized MLevel getLevel() {
            if (this.myLevel == null) {
                this.myLevel = this.guessMLevel();
            }
            return this.myLevel;
        }

        @Override
        public boolean isLoggable(MLevel mLevel) {
            return this.levelLogger(mLevel) != this.offL;
        }

        @Override
        public String getName() {
            return this.logger.getName();
        }

        @Override
        public void addHandler(Object object) throws SecurityException {
            throw new UnsupportedOperationException("Handlers not supported; the 'handler' " + object + " is not compatible with MLogger " + this);
        }

        @Override
        public void removeHandler(Object object) throws SecurityException {
            throw new UnsupportedOperationException("Handlers not supported; the 'handler' " + object + " is not compatible with MLogger " + this);
        }

        @Override
        public Object[] getHandlers() {
            return EMPTY_OBJ_ARRAY;
        }

        @Override
        public void setUseParentHandlers(boolean bl) {
            throw new UnsupportedOperationException("Handlers not supported.");
        }

        @Override
        public boolean getUseParentHandlers() {
            throw new UnsupportedOperationException("Handlers not supported.");
        }

        private class ErrorLogger
        implements LevelLogger {
            private ErrorLogger() {
            }

            @Override
            public void log(String string) {
                Slf4jMLogger.this.logger.error(string);
            }

            @Override
            public void log(String string, Object object) {
                Slf4jMLogger.this.logger.error(string, object);
            }

            @Override
            public void log(String string, Object[] objectArray) {
                Slf4jMLogger.this.logger.error(string, objectArray);
            }

            @Override
            public void log(String string, Throwable throwable) {
                Slf4jMLogger.this.logger.error(string, throwable);
            }
        }

        private class WarnLogger
        implements LevelLogger {
            private WarnLogger() {
            }

            @Override
            public void log(String string) {
                Slf4jMLogger.this.logger.warn(string);
            }

            @Override
            public void log(String string, Object object) {
                Slf4jMLogger.this.logger.warn(string, object);
            }

            @Override
            public void log(String string, Object[] objectArray) {
                Slf4jMLogger.this.logger.warn(string, objectArray);
            }

            @Override
            public void log(String string, Throwable throwable) {
                Slf4jMLogger.this.logger.warn(string, throwable);
            }
        }

        private class InfoLogger
        implements LevelLogger {
            private InfoLogger() {
            }

            @Override
            public void log(String string) {
                Slf4jMLogger.this.logger.info(string);
            }

            @Override
            public void log(String string, Object object) {
                Slf4jMLogger.this.logger.info(string, object);
            }

            @Override
            public void log(String string, Object[] objectArray) {
                Slf4jMLogger.this.logger.info(string, objectArray);
            }

            @Override
            public void log(String string, Throwable throwable) {
                Slf4jMLogger.this.logger.info(string, throwable);
            }
        }

        private class DebugLogger
        implements LevelLogger {
            private DebugLogger() {
            }

            @Override
            public void log(String string) {
                Slf4jMLogger.this.logger.debug(string);
            }

            @Override
            public void log(String string, Object object) {
                Slf4jMLogger.this.logger.debug(string, object);
            }

            @Override
            public void log(String string, Object[] objectArray) {
                Slf4jMLogger.this.logger.debug(string, objectArray);
            }

            @Override
            public void log(String string, Throwable throwable) {
                Slf4jMLogger.this.logger.debug(string, throwable);
            }
        }

        private class TraceLogger
        implements LevelLogger {
            private TraceLogger() {
            }

            @Override
            public void log(String string) {
                Slf4jMLogger.this.logger.trace(string);
            }

            @Override
            public void log(String string, Object object) {
                Slf4jMLogger.this.logger.trace(string, object);
            }

            @Override
            public void log(String string, Object[] objectArray) {
                Slf4jMLogger.this.logger.trace(string, objectArray);
            }

            @Override
            public void log(String string, Throwable throwable) {
                Slf4jMLogger.this.logger.trace(string, throwable);
            }
        }

        private class OffLogger
        implements LevelLogger {
            private OffLogger() {
            }

            @Override
            public void log(String string) {
            }

            @Override
            public void log(String string, Object object) {
            }

            @Override
            public void log(String string, Object[] objectArray) {
            }

            @Override
            public void log(String string, Throwable throwable) {
            }
        }

        private static interface LevelLogger {
            public void log(String var1);

            public void log(String var1, Object var2);

            public void log(String var1, Object[] var2);

            public void log(String var1, Throwable var2);
        }
    }
}

