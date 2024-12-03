/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log.Hierarchy
 *  org.apache.log.Logger
 */
package freemarker.log;

import freemarker.log.Logger;
import freemarker.log.LoggerFactory;
import org.apache.log.Hierarchy;

public class _AvalonLoggerFactory
implements LoggerFactory {
    @Override
    public Logger getLogger(String category) {
        return new AvalonLogger(Hierarchy.getDefaultHierarchy().getLoggerFor(category));
    }

    private static class AvalonLogger
    extends Logger {
        private final org.apache.log.Logger logger;

        AvalonLogger(org.apache.log.Logger logger) {
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
        public void error(String message) {
            this.logger.error(message);
        }

        @Override
        public void error(String message, Throwable t) {
            this.logger.error(message, t);
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
            return this.logger.isFatalErrorEnabled();
        }
    }
}

