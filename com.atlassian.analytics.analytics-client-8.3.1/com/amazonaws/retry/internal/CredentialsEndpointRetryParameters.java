/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.retry.internal;

import com.amazonaws.annotation.SdkInternalApi;

@SdkInternalApi
public class CredentialsEndpointRetryParameters {
    private final Integer statusCode;
    private final Exception exception;

    private CredentialsEndpointRetryParameters(Builder builder) {
        this.statusCode = builder.statusCode;
        this.exception = builder.exception;
    }

    public Integer getStatusCode() {
        return this.statusCode;
    }

    public Exception getException() {
        return this.exception;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Integer statusCode;
        private final Exception exception;

        private Builder() {
            this.statusCode = null;
            this.exception = null;
        }

        private Builder(Integer statusCode, Exception exception) {
            this.statusCode = statusCode;
            this.exception = exception;
        }

        public Builder withStatusCode(Integer statusCode) {
            return new Builder(statusCode, this.exception);
        }

        public Builder withException(Exception exception) {
            return new Builder(this.statusCode, exception);
        }

        public CredentialsEndpointRetryParameters build() {
            return new CredentialsEndpointRetryParameters(this);
        }
    }
}

