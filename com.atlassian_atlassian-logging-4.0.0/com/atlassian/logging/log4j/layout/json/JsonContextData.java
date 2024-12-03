/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.logging.log4j.layout.json;

public class JsonContextData {
    private final String requestId;
    private final String sessionId;
    private final String userKey;

    private JsonContextData(Builder builder) {
        this.requestId = builder.requestId;
        this.sessionId = builder.sessionId;
        this.userKey = builder.userKey;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public String getUserKey() {
        return this.userKey;
    }

    public boolean isEmpty() {
        return this.requestId == null && this.sessionId == null && this.userKey == null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String requestId;
        private String sessionId;
        private String userKey;

        public Builder setRequestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder setSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder setUserKey(String userKey) {
            this.userKey = userKey;
            return this;
        }

        public JsonContextData build() {
            return new JsonContextData(this);
        }
    }
}

