/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.scheduling.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.support.DelegatingErrorHandlingRunnable;
import org.springframework.util.ErrorHandler;
import org.springframework.util.ReflectionUtils;

public abstract class TaskUtils {
    public static final ErrorHandler LOG_AND_SUPPRESS_ERROR_HANDLER = new LoggingErrorHandler();
    public static final ErrorHandler LOG_AND_PROPAGATE_ERROR_HANDLER = new PropagatingErrorHandler();

    public static DelegatingErrorHandlingRunnable decorateTaskWithErrorHandler(Runnable task, @Nullable ErrorHandler errorHandler, boolean isRepeatingTask) {
        if (task instanceof DelegatingErrorHandlingRunnable) {
            return (DelegatingErrorHandlingRunnable)task;
        }
        ErrorHandler eh = errorHandler != null ? errorHandler : TaskUtils.getDefaultErrorHandler(isRepeatingTask);
        return new DelegatingErrorHandlingRunnable(task, eh);
    }

    public static ErrorHandler getDefaultErrorHandler(boolean isRepeatingTask) {
        return isRepeatingTask ? LOG_AND_SUPPRESS_ERROR_HANDLER : LOG_AND_PROPAGATE_ERROR_HANDLER;
    }

    private static class PropagatingErrorHandler
    extends LoggingErrorHandler {
        private PropagatingErrorHandler() {
        }

        @Override
        public void handleError(Throwable t) {
            super.handleError(t);
            ReflectionUtils.rethrowRuntimeException(t);
        }
    }

    private static class LoggingErrorHandler
    implements ErrorHandler {
        private final Log logger = LogFactory.getLog(LoggingErrorHandler.class);

        private LoggingErrorHandler() {
        }

        @Override
        public void handleError(Throwable t) {
            this.logger.error((Object)"Unexpected error occurred in scheduled task", t);
        }
    }
}

