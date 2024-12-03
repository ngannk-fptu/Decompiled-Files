/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Level
 *  org.apache.log4j.Logger
 *  org.apache.log4j.Priority
 */
package freemarker.log;

import freemarker.log.Logger;
import freemarker.log.LoggerFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;

public class _Log4jLoggerFactory
implements LoggerFactory {
    @Override
    public Logger getLogger(String category) {
        return new Log4jLogger(org.apache.log4j.Logger.getLogger((String)category));
    }

    private static class Log4jLogger
    extends Logger {
        private final org.apache.log4j.Logger logger;

        Log4jLogger(org.apache.log4j.Logger logger) {
            this.logger = logger;
        }

        @Override
        public void debug(String message) {
            this.logger.debug((Object)message);
        }

        @Override
        public void debug(String message, Throwable t) {
            this.logger.debug((Object)message, t);
        }

        @Override
        public void error(String message) {
            this.logger.error((Object)message);
        }

        @Override
        public void error(String message, Throwable t) {
            this.logger.error((Object)message, t);
        }

        @Override
        public void info(String message) {
            this.logger.info((Object)message);
        }

        @Override
        public void info(String message, Throwable t) {
            this.logger.info((Object)message, t);
        }

        @Override
        public void warn(String message) {
            this.logger.warn((Object)message);
        }

        @Override
        public void warn(String message, Throwable t) {
            this.logger.warn((Object)message, t);
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
            return this.logger.isEnabledFor((Priority)Level.WARN);
        }

        @Override
        public boolean isErrorEnabled() {
            return this.logger.isEnabledFor((Priority)Level.ERROR);
        }

        @Override
        public boolean isFatalEnabled() {
            return this.logger.isEnabledFor((Priority)Level.FATAL);
        }
    }
}

