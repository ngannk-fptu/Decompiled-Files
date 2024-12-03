/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.spi.ExtendedLogger
 */
package com.hazelcast.logging;

import com.hazelcast.logging.AbstractLogger;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LogEvent;
import com.hazelcast.logging.LoggerFactorySupport;
import com.hazelcast.spi.annotation.PrivateApi;
import java.util.logging.LogRecord;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class Log4j2Factory
extends LoggerFactorySupport {
    private static final String FQCN = Log4j2Logger.class.getName();

    @Override
    protected ILogger createLogger(String name) {
        return new Log4j2Logger(LogManager.getContext().getLogger(name));
    }

    @PrivateApi
    public static class Log4j2Logger
    extends AbstractLogger {
        private final ExtendedLogger logger;

        public Log4j2Logger(ExtendedLogger logger) {
            this.logger = logger;
        }

        @Override
        public void log(LogEvent logEvent) {
            LogRecord logRecord = logEvent.getLogRecord();
            java.util.logging.Level level = logEvent.getLogRecord().getLevel();
            String message = logRecord.getMessage();
            Throwable thrown = logRecord.getThrown();
            this.log(level, message, thrown);
        }

        @Override
        public void log(java.util.logging.Level level, String message) {
            this.logger.logIfEnabled(FQCN, Log4j2Logger.toLog4j2Level(level), null, message);
        }

        @Override
        public void log(java.util.logging.Level level, String message, Throwable thrown) {
            this.logger.logIfEnabled(FQCN, Log4j2Logger.toLog4j2Level(level), null, message, thrown);
        }

        @Override
        public java.util.logging.Level getLevel() {
            return this.logger.isTraceEnabled() ? java.util.logging.Level.FINEST : (this.logger.isDebugEnabled() ? java.util.logging.Level.FINE : (this.logger.isInfoEnabled() ? java.util.logging.Level.INFO : (this.logger.isWarnEnabled() ? java.util.logging.Level.WARNING : (this.logger.isErrorEnabled() ? java.util.logging.Level.SEVERE : (this.logger.isFatalEnabled() ? java.util.logging.Level.SEVERE : java.util.logging.Level.OFF)))));
        }

        @Override
        public boolean isLoggable(java.util.logging.Level level) {
            return level != java.util.logging.Level.OFF && this.logger.isEnabled(Log4j2Logger.toLog4j2Level(level), null);
        }

        private static Level toLog4j2Level(java.util.logging.Level level) {
            return level == java.util.logging.Level.FINEST ? Level.TRACE : (level == java.util.logging.Level.FINE ? Level.DEBUG : (level == java.util.logging.Level.INFO ? Level.INFO : (level == java.util.logging.Level.WARNING ? Level.WARN : (level == java.util.logging.Level.SEVERE ? Level.ERROR : (level == java.util.logging.Level.FINER ? Level.DEBUG : (level == java.util.logging.Level.CONFIG ? Level.INFO : (level == java.util.logging.Level.OFF ? Level.OFF : Level.INFO)))))));
        }
    }
}

