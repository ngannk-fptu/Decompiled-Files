/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Category
 *  org.apache.log4j.Level
 *  org.apache.log4j.Logger
 *  org.apache.log4j.Priority
 *  org.apache.log4j.spi.LoggingEvent
 */
package com.hazelcast.logging;

import com.hazelcast.logging.AbstractLogger;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LogEvent;
import com.hazelcast.logging.LoggerFactory;
import com.hazelcast.logging.LoggerFactorySupport;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

public class Log4jFactory
extends LoggerFactorySupport
implements LoggerFactory {
    @Override
    protected ILogger createLogger(String name) {
        Logger l = Logger.getLogger((String)name);
        return new Log4jLogger(l);
    }

    static class Log4jLogger
    extends AbstractLogger {
        private final Logger logger;
        private final Level level;

        Log4jLogger(Logger logger) {
            this.logger = logger;
            org.apache.log4j.Level log4jLevel = logger.getLevel();
            this.level = Log4jLogger.toStandardLevel(log4jLevel);
        }

        @Override
        public void log(Level level, String message) {
            this.logger.log((Priority)Log4jLogger.toLog4jLevel(level), (Object)message);
        }

        @Override
        public void log(Level level, String message, Throwable thrown) {
            this.logger.log((Priority)Log4jLogger.toLog4jLevel(level), (Object)message, thrown);
        }

        @Override
        public Level getLevel() {
            return this.level;
        }

        @Override
        public boolean isLoggable(Level level) {
            return level != Level.OFF && this.logger.isEnabledFor((Priority)Log4jLogger.toLog4jLevel(level));
        }

        @Override
        public void log(LogEvent logEvent) {
            LogRecord logRecord = logEvent.getLogRecord();
            Level eventLevel = logRecord.getLevel();
            if (eventLevel == Level.OFF) {
                return;
            }
            String name = logEvent.getLogRecord().getLoggerName();
            Logger logger = Logger.getLogger((String)name);
            org.apache.log4j.Level level = Log4jLogger.toLog4jLevel(eventLevel);
            String message = logRecord.getMessage();
            Throwable throwable = logRecord.getThrown();
            logger.callAppenders(new LoggingEvent(name, (Category)logger, (Priority)level, (Object)message, throwable));
        }

        private static org.apache.log4j.Level toLog4jLevel(Level level) {
            return level == Level.FINEST ? org.apache.log4j.Level.TRACE : (level == Level.FINE ? org.apache.log4j.Level.DEBUG : (level == Level.INFO ? org.apache.log4j.Level.INFO : (level == Level.WARNING ? org.apache.log4j.Level.WARN : (level == Level.SEVERE ? org.apache.log4j.Level.ERROR : (level == Level.CONFIG ? org.apache.log4j.Level.INFO : (level == Level.FINER ? org.apache.log4j.Level.DEBUG : (level == Level.OFF ? org.apache.log4j.Level.OFF : org.apache.log4j.Level.INFO)))))));
        }

        private static Level toStandardLevel(org.apache.log4j.Level log4jLevel) {
            return log4jLevel == org.apache.log4j.Level.TRACE ? Level.FINEST : (log4jLevel == org.apache.log4j.Level.DEBUG ? Level.FINE : (log4jLevel == org.apache.log4j.Level.INFO ? Level.INFO : (log4jLevel == org.apache.log4j.Level.WARN ? Level.WARNING : (log4jLevel == org.apache.log4j.Level.ERROR ? Level.SEVERE : (log4jLevel == org.apache.log4j.Level.FATAL ? Level.SEVERE : (log4jLevel == org.apache.log4j.Level.OFF ? Level.OFF : Level.INFO))))));
        }
    }
}

