/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl;

import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationResponseHandler;

public final class OperationResponseHandlerFactory {
    private static final NoResponseHandler EMPTY_RESPONSE_HANDLER = new NoResponseHandler();

    private OperationResponseHandlerFactory() {
    }

    public static OperationResponseHandler createEmptyResponseHandler() {
        return EMPTY_RESPONSE_HANDLER;
    }

    public static OperationResponseHandler createErrorLoggingResponseHandler(ILogger logger) {
        return new ErrorLoggingResponseHandler(logger);
    }

    private static final class ErrorLoggingResponseHandler
    implements OperationResponseHandler {
        private final ILogger logger;

        private ErrorLoggingResponseHandler(ILogger logger) {
            this.logger = logger;
        }

        public void sendResponse(Operation op, Object obj) {
            if (obj instanceof Throwable) {
                Throwable t = (Throwable)obj;
                this.logger.severe(t);
            }
        }
    }

    private static class NoResponseHandler
    implements OperationResponseHandler {
        private NoResponseHandler() {
        }

        public void sendResponse(Operation op, Object obj) {
        }
    }
}

