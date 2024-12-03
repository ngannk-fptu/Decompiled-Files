/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.logging;

import com.hazelcast.logging.AbstractLogger;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LogEvent;
import com.hazelcast.logging.LoggerFactory;
import com.hazelcast.logging.LoggerFactorySupport;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class StandardLoggerFactory
extends LoggerFactorySupport
implements LoggerFactory {
    @Override
    protected ILogger createLogger(String name) {
        Logger l = Logger.getLogger(name);
        return new StandardLogger(l);
    }

    static class StandardLogger
    extends AbstractLogger {
        private final Logger logger;

        public StandardLogger(Logger logger) {
            this.logger = logger;
        }

        @Override
        public void log(Level level, String message) {
            this.log(level, message, null);
        }

        @Override
        public void log(Level level, String message, Throwable thrown) {
            LogRecord logRecord = new LogRecord(level, message);
            logRecord.setLoggerName(this.logger.getName());
            logRecord.setThrown(thrown);
            logRecord.setSourceClassName(this.logger.getName());
            this.logger.log(logRecord);
        }

        @Override
        public void log(LogEvent logEvent) {
            this.logger.log(logEvent.getLogRecord());
        }

        @Override
        public Level getLevel() {
            return this.logger.getLevel();
        }

        @Override
        public boolean isLoggable(Level level) {
            return this.logger.isLoggable(level);
        }
    }
}

