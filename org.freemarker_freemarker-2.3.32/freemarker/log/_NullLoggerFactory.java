/*
 * Decompiled with CFR 0.152.
 */
package freemarker.log;

import freemarker.log.Logger;
import freemarker.log.LoggerFactory;

public class _NullLoggerFactory
implements LoggerFactory {
    private static final Logger INSTANCE = new Logger(){

        @Override
        public void debug(String message) {
        }

        @Override
        public void debug(String message, Throwable t) {
        }

        @Override
        public void error(String message) {
        }

        @Override
        public void error(String message, Throwable t) {
        }

        @Override
        public void info(String message) {
        }

        @Override
        public void info(String message, Throwable t) {
        }

        @Override
        public void warn(String message) {
        }

        @Override
        public void warn(String message, Throwable t) {
        }

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public boolean isInfoEnabled() {
            return false;
        }

        @Override
        public boolean isWarnEnabled() {
            return false;
        }

        @Override
        public boolean isErrorEnabled() {
            return false;
        }

        @Override
        public boolean isFatalEnabled() {
            return false;
        }
    };

    _NullLoggerFactory() {
    }

    @Override
    public Logger getLogger(String category) {
        return INSTANCE;
    }
}

