/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.hazelcast.logging;

import com.hazelcast.logging.AbstractLogger;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LogEvent;
import com.hazelcast.logging.LoggerFactorySupport;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jFactory
extends LoggerFactorySupport {
    @Override
    protected ILogger createLogger(String name) {
        Logger logger = LoggerFactory.getLogger((String)name);
        return new Slf4jLogger(logger);
    }

    static class Slf4jLogger
    extends AbstractLogger {
        private final Logger logger;

        Slf4jLogger(Logger logger) {
            this.logger = logger;
        }

        @Override
        public void log(Level level, String message) {
            if (level == Level.FINEST) {
                this.logger.trace(message);
            } else if (level == Level.FINER || level == Level.FINE) {
                this.logger.debug(message);
            } else if (level == Level.CONFIG || level == Level.INFO) {
                this.logger.info(message);
            } else if (level == Level.WARNING) {
                this.logger.warn(message);
            } else if (level == Level.SEVERE) {
                this.logger.error(message);
            } else if (level != Level.OFF) {
                this.logger.info(message);
            }
        }

        @Override
        public void log(Level level, String message, Throwable thrown) {
            if (level == Level.FINEST) {
                this.logger.trace(message, thrown);
            } else if (level == Level.FINER || level == Level.FINE) {
                this.logger.debug(message, thrown);
            } else if (level == Level.CONFIG || level == Level.INFO) {
                this.logger.info(message, thrown);
            } else if (level == Level.WARNING) {
                this.logger.warn(message, thrown);
            } else if (level == Level.SEVERE) {
                this.logger.error(message, thrown);
            } else if (level != Level.OFF) {
                this.logger.info(message, thrown);
            }
        }

        @Override
        public Level getLevel() {
            return this.logger.isTraceEnabled() ? Level.FINEST : (this.logger.isDebugEnabled() ? Level.FINE : (this.logger.isInfoEnabled() ? Level.INFO : (this.logger.isWarnEnabled() ? Level.WARNING : (this.logger.isErrorEnabled() ? Level.SEVERE : Level.OFF))));
        }

        @Override
        public boolean isLoggable(Level level) {
            return level == Level.FINEST ? this.logger.isTraceEnabled() : (level == Level.FINER ? this.logger.isDebugEnabled() : (level == Level.FINE ? this.logger.isDebugEnabled() : (level == Level.CONFIG ? this.logger.isInfoEnabled() : (level == Level.INFO ? this.logger.isInfoEnabled() : (level == Level.WARNING ? this.logger.isWarnEnabled() : (level == Level.SEVERE ? this.logger.isErrorEnabled() : level != Level.OFF && this.logger.isInfoEnabled()))))));
        }

        @Override
        public void log(LogEvent logEvent) {
            LogRecord logRecord = logEvent.getLogRecord();
            Level level = logEvent.getLogRecord().getLevel();
            String message = logRecord.getMessage();
            Throwable thrown = logRecord.getThrown();
            this.log(level, message, thrown);
        }
    }
}

