/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package io.atlassian.fugue.retry;

import io.atlassian.fugue.retry.ExceptionHandler;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionHandlers {
    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlers.class);

    private ExceptionHandlers() {
        throw new AssertionError((Object)"This class is not instantiable.");
    }

    public static ExceptionHandler loggingExceptionHandler(Logger logger) {
        return new LoggingExceptionHandler(logger == null ? log : logger);
    }

    public static ExceptionHandler ignoreExceptionHandler() {
        return new IgnoreExceptionHandler();
    }

    public static ExceptionHandler chain(ExceptionHandler ... handlers) {
        return new CompositeExceptionHandler(handlers);
    }

    static Logger logger() {
        return log;
    }

    private static class CompositeExceptionHandler
    implements ExceptionHandler {
        private final ExceptionHandler[] handlers;

        public CompositeExceptionHandler(ExceptionHandler ... handlers) {
            Objects.requireNonNull(handlers);
            this.handlers = handlers;
        }

        @Override
        public void handle(RuntimeException e) {
            for (ExceptionHandler handler : this.handlers) {
                handler.handle(e);
            }
        }
    }

    static class LoggingExceptionHandler
    implements ExceptionHandler {
        private final Logger logger;

        LoggingExceptionHandler(Logger logger) {
            this.logger = logger;
        }

        @Override
        public void handle(RuntimeException e) {
            this.warn(this.logger, e);
        }

        private void warn(Logger log, Exception e) {
            log.warn("Exception encountered: ", (Throwable)e);
        }

        Logger logger() {
            return this.logger;
        }
    }

    private static class IgnoreExceptionHandler
    implements ExceptionHandler {
        private IgnoreExceptionHandler() {
        }

        @Override
        public void handle(RuntimeException a) {
        }
    }
}

