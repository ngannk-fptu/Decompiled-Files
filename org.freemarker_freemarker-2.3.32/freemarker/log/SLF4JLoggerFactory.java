/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.spi.LocationAwareLogger
 */
package freemarker.log;

import freemarker.log.Logger;
import freemarker.log.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

@Deprecated
public class SLF4JLoggerFactory
implements LoggerFactory {
    @Override
    public Logger getLogger(String category) {
        org.slf4j.Logger slf4jLogger = org.slf4j.LoggerFactory.getLogger((String)category);
        if (slf4jLogger instanceof LocationAwareLogger) {
            return new LocationAwareSLF4JLogger((LocationAwareLogger)slf4jLogger);
        }
        return new LocationUnawareSLF4JLogger(slf4jLogger);
    }

    private static class LocationUnawareSLF4JLogger
    extends Logger {
        private final org.slf4j.Logger logger;

        LocationUnawareSLF4JLogger(org.slf4j.Logger logger) {
            this.logger = logger;
        }

        @Override
        public void debug(String message) {
            this.logger.debug(message);
        }

        @Override
        public void debug(String message, Throwable t) {
            this.logger.debug(message, t);
        }

        @Override
        public void info(String message) {
            this.logger.info(message);
        }

        @Override
        public void info(String message, Throwable t) {
            this.logger.info(message, t);
        }

        @Override
        public void warn(String message) {
            this.logger.warn(message);
        }

        @Override
        public void warn(String message, Throwable t) {
            this.logger.warn(message, t);
        }

        @Override
        public void error(String message) {
            this.logger.error(message);
        }

        @Override
        public void error(String message, Throwable t) {
            this.logger.error(message, t);
        }

        @Override
        public boolean isDebugEnabled() {
            return this.logger.isDebugEnabled();
        }

        @Override
        public boolean isInfoEnabled() {
            return this.logger.isInfoEnabled();
        }

        @Override
        public boolean isWarnEnabled() {
            return this.logger.isWarnEnabled();
        }

        @Override
        public boolean isErrorEnabled() {
            return this.logger.isErrorEnabled();
        }

        @Override
        public boolean isFatalEnabled() {
            return this.logger.isErrorEnabled();
        }
    }

    private static final class LocationAwareSLF4JLogger
    extends Logger {
        private static final String ADAPTER_FQCN = LocationAwareSLF4JLogger.class.getName();
        private final LocationAwareLogger logger;

        LocationAwareSLF4JLogger(LocationAwareLogger logger) {
            this.logger = logger;
        }

        @Override
        public void debug(String message) {
            this.debug(message, null);
        }

        @Override
        public void debug(String message, Throwable t) {
            this.logger.log(null, ADAPTER_FQCN, 10, message, null, t);
        }

        @Override
        public void info(String message) {
            this.info(message, null);
        }

        @Override
        public void info(String message, Throwable t) {
            this.logger.log(null, ADAPTER_FQCN, 20, message, null, t);
        }

        @Override
        public void warn(String message) {
            this.warn(message, null);
        }

        @Override
        public void warn(String message, Throwable t) {
            this.logger.log(null, ADAPTER_FQCN, 30, message, null, t);
        }

        @Override
        public void error(String message) {
            this.error(message, null);
        }

        @Override
        public void error(String message, Throwable t) {
            this.logger.log(null, ADAPTER_FQCN, 40, message, null, t);
        }

        @Override
        public boolean isDebugEnabled() {
            return this.logger.isDebugEnabled();
        }

        @Override
        public boolean isInfoEnabled() {
            return this.logger.isInfoEnabled();
        }

        @Override
        public boolean isWarnEnabled() {
            return this.logger.isWarnEnabled();
        }

        @Override
        public boolean isErrorEnabled() {
            return this.logger.isErrorEnabled();
        }

        @Override
        public boolean isFatalEnabled() {
            return this.logger.isErrorEnabled();
        }
    }
}

