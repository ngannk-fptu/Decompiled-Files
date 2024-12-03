/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.handlers;

import com.amazonaws.Request;

public final class HandlerBeforeAttemptContext {
    private final Request<?> request;

    private HandlerBeforeAttemptContext(Request<?> request) {
        this.request = request;
    }

    public Request<?> getRequest() {
        return this.request;
    }

    public static HandlerBeforeAttemptContextBuilder builder() {
        return new HandlerBeforeAttemptContextBuilder();
    }

    public static class HandlerBeforeAttemptContextBuilder {
        private Request<?> request;

        private HandlerBeforeAttemptContextBuilder() {
        }

        public HandlerBeforeAttemptContextBuilder withRequest(Request<?> request) {
            this.request = request;
            return this;
        }

        public HandlerBeforeAttemptContext build() {
            return new HandlerBeforeAttemptContext(this.request);
        }
    }
}

