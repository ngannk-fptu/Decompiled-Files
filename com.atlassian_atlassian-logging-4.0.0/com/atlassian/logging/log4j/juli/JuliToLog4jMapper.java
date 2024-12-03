/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.Level
 */
package com.atlassian.logging.log4j.juli;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

public class JuliToLog4jMapper {
    private static Map<java.util.logging.Level, Level> levelMap = new HashMap<java.util.logging.Level, Level>();
    private final SimpleFormatter formatter = new SimpleFormatter();

    public MappedLogRecord map(LogRecord lr) {
        return new MappedLogRecord(this.mapCalledFQCN(lr), this.mapLoggerName(lr), this.mapLevel(lr), this.mapMessage(lr), this.mapThrowable(lr));
    }

    protected Level mapLevel(@Nonnull LogRecord lr) {
        Level lvl = levelMap.get(lr.getLevel());
        return lvl == null ? Level.ERROR : lvl;
    }

    protected String mapMessage(@Nonnull LogRecord lr) {
        return this.formatter.formatMessage(lr);
    }

    protected String mapLoggerName(@Nonnull LogRecord lr) {
        return lr.getLoggerName();
    }

    protected String mapCalledFQCN(@Nonnull LogRecord lr) {
        return StringUtils.defaultString((String)lr.getSourceClassName());
    }

    protected Throwable mapThrowable(@Nonnull LogRecord lr) {
        return lr.getThrown();
    }

    static {
        levelMap.put(java.util.logging.Level.ALL, Level.ALL);
        levelMap.put(java.util.logging.Level.FINEST, Level.TRACE);
        levelMap.put(java.util.logging.Level.FINER, Level.DEBUG);
        levelMap.put(java.util.logging.Level.FINE, Level.DEBUG);
        levelMap.put(java.util.logging.Level.INFO, Level.INFO);
        levelMap.put(java.util.logging.Level.CONFIG, Level.INFO);
        levelMap.put(java.util.logging.Level.WARNING, Level.WARN);
        levelMap.put(java.util.logging.Level.SEVERE, Level.ERROR);
        levelMap.put(java.util.logging.Level.OFF, Level.OFF);
    }

    public static class MappedLogRecord {
        private final String callerFQCN;
        private final String loggerName;
        private final Level level;
        private final String message;
        private final Throwable throwable;

        public MappedLogRecord(String callerFQCN, String loggerName, Level level, String message, Throwable throwable) {
            this.callerFQCN = callerFQCN;
            this.loggerName = loggerName;
            this.level = level;
            this.message = message;
            this.throwable = throwable;
        }

        public String getCallerFQCN() {
            return this.callerFQCN;
        }

        public Level getLevel() {
            return this.level;
        }

        public String getLoggerName() {
            return this.loggerName;
        }

        public String getMessage() {
            return this.message;
        }

        public Throwable getThrowable() {
            return this.throwable;
        }
    }
}

