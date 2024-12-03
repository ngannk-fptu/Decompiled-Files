/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.logging;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LogEvent;
import com.hazelcast.logging.LoggerFactory;
import java.util.logging.Level;

public class NoLogFactory
implements LoggerFactory {
    private final ILogger noLogger = new NoLogger();

    @Override
    public ILogger getLogger(String name) {
        return this.noLogger;
    }

    static class NoLogger
    implements ILogger {
        NoLogger() {
        }

        @Override
        public void finest(String message) {
        }

        @Override
        public void finest(Throwable thrown) {
        }

        @Override
        public void finest(String message, Throwable thrown) {
        }

        @Override
        public boolean isFinestEnabled() {
            return false;
        }

        @Override
        public void fine(String message) {
        }

        @Override
        public void fine(Throwable thrown) {
        }

        @Override
        public void fine(String message, Throwable thrown) {
        }

        @Override
        public boolean isFineEnabled() {
            return false;
        }

        @Override
        public void info(String message) {
        }

        @Override
        public void info(String message, Throwable thrown) {
        }

        @Override
        public void info(Throwable thrown) {
        }

        @Override
        public boolean isInfoEnabled() {
            return false;
        }

        @Override
        public void warning(String message) {
        }

        @Override
        public void warning(Throwable thrown) {
        }

        @Override
        public void warning(String message, Throwable thrown) {
        }

        @Override
        public boolean isWarningEnabled() {
            return false;
        }

        @Override
        public void severe(String message) {
        }

        @Override
        public void severe(Throwable thrown) {
        }

        @Override
        public void severe(String message, Throwable thrown) {
        }

        @Override
        public boolean isSevereEnabled() {
            return false;
        }

        @Override
        public void log(Level level, String message) {
        }

        @Override
        public void log(Level level, String message, Throwable thrown) {
        }

        @Override
        public void log(LogEvent logEvent) {
        }

        @Override
        public Level getLevel() {
            return Level.OFF;
        }

        @Override
        public boolean isLoggable(Level level) {
            return false;
        }
    }
}

