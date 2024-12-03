/*
 * Decompiled with CFR 0.152.
 */
package freemarker.log;

import freemarker.log.Logger;
import freemarker.log.LoggerFactory;
import java.util.logging.Level;

public class _JULLoggerFactory
implements LoggerFactory {
    @Override
    public Logger getLogger(String category) {
        return new JULLogger(java.util.logging.Logger.getLogger(category));
    }

    private static class JULLogger
    extends Logger {
        private final java.util.logging.Logger logger;

        JULLogger(java.util.logging.Logger logger) {
            this.logger = logger;
        }

        @Override
        public void debug(String message) {
            this.logger.log(Level.FINE, message);
        }

        @Override
        public void debug(String message, Throwable t) {
            this.logger.log(Level.FINE, message, t);
        }

        @Override
        public void error(String message) {
            this.logger.log(Level.SEVERE, message);
        }

        @Override
        public void error(String message, Throwable t) {
            this.logger.log(Level.SEVERE, message, t);
        }

        @Override
        public void info(String message) {
            this.logger.log(Level.INFO, message);
        }

        @Override
        public void info(String message, Throwable t) {
            this.logger.log(Level.INFO, message, t);
        }

        @Override
        public void warn(String message) {
            this.logger.log(Level.WARNING, message);
        }

        @Override
        public void warn(String message, Throwable t) {
            this.logger.log(Level.WARNING, message, t);
        }

        @Override
        public boolean isDebugEnabled() {
            return this.logger.isLoggable(Level.FINE);
        }

        @Override
        public boolean isInfoEnabled() {
            return this.logger.isLoggable(Level.INFO);
        }

        @Override
        public boolean isWarnEnabled() {
            return this.logger.isLoggable(Level.WARNING);
        }

        @Override
        public boolean isErrorEnabled() {
            return this.logger.isLoggable(Level.SEVERE);
        }

        @Override
        public boolean isFatalEnabled() {
            return this.logger.isLoggable(Level.SEVERE);
        }
    }
}

