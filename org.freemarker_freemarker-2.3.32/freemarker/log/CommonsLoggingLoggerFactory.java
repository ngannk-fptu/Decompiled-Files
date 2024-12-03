/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package freemarker.log;

import freemarker.log.Logger;
import freemarker.log.LoggerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Deprecated
public class CommonsLoggingLoggerFactory
implements LoggerFactory {
    @Override
    public Logger getLogger(String category) {
        return new CommonsLoggingLogger(LogFactory.getLog((String)category));
    }

    private static class CommonsLoggingLogger
    extends Logger {
        private final Log logger;

        CommonsLoggingLogger(Log logger) {
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
        public void error(String message) {
            this.logger.error((Object)message);
        }

        @Override
        public void error(String message, Throwable t) {
            this.logger.error((Object)message, t);
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
            return this.logger.isFatalEnabled();
        }
    }
}

