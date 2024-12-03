/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions.util;

import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public final class ResourcesEndpointRetryParameters {
    private final Integer statusCode;
    private final Exception exception;

    private ResourcesEndpointRetryParameters(Builder builder) {
        this.statusCode = builder.statusCode;
        this.exception = builder.exception;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer getStatusCode() {
        return this.statusCode;
    }

    public Exception getException() {
        return this.exception;
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

        public ResourcesEndpointRetryParameters build() {
            return new ResourcesEndpointRetryParameters(this);
        }
    }
}

