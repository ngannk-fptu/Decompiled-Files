/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.handlers;

import com.amazonaws.Request;
import com.amazonaws.Response;

public final class HandlerAfterAttemptContext {
    private final Request<?> request;
    private final Response<?> response;
    private final Exception exception;

    private HandlerAfterAttemptContext(Request<?> request, Response<?> response, Exception exception) {
        this.request = request;
        this.response = response;
        this.exception = exception;
    }

    public Request<?> getRequest() {
        return this.request;
    }

    public Response<?> getResponse() {
        return this.response;
    }

    public Exception getException() {
        return this.exception;
    }

    public static HandlerAfterAttemptContextBuilder builder() {
        return new HandlerAfterAttemptContextBuilder();
    }

    public static class HandlerAfterAttemptContextBuilder {
        private Request<?> request;
        private Response<?> response;
        private Exception exception;

        private HandlerAfterAttemptContextBuilder() {
        }

        public HandlerAfterAttemptContextBuilder withRequest(Request<?> request) {
            this.request = request;
            return this;
        }

        public HandlerAfterAttemptContextBuilder withResponse(Response<?> response) {
            this.response = response;
            return this;
        }

        public HandlerAfterAttemptContextBuilder withException(Exception exception) {
            this.exception = exception;
            return this;
        }

        public HandlerAfterAttemptContext build() {
            return new HandlerAfterAttemptContext(this.request, this.response, this.exception);
        }
    }
}

