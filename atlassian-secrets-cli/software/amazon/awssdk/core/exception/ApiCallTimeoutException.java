/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.exception;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.exception.SdkClientException;

@SdkPublicApi
public final class ApiCallTimeoutException
extends SdkClientException {
    private static final long serialVersionUID = 1L;

    private ApiCallTimeoutException(Builder b) {
        super(b);
    }

    public static ApiCallTimeoutException create(long timeout) {
        return ApiCallTimeoutException.builder().message(String.format("Client execution did not complete before the specified timeout configuration: %s millis", timeout)).build();
    }

    public static ApiCallTimeoutException create(String message, Throwable cause) {
        return ApiCallTimeoutException.builder().message(message).cause(cause).build();
    }

    @Override
    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    protected static final class BuilderImpl
    extends SdkClientException.BuilderImpl
    implements Builder {
        protected BuilderImpl() {
        }

        protected BuilderImpl(ApiCallTimeoutException ex) {
            super(ex);
        }

        @Override
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        @Override
        public Builder cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        @Override
        public Builder writableStackTrace(Boolean writableStackTrace) {
            this.writableStackTrace = writableStackTrace;
            return this;
        }

        @Override
        public ApiCallTimeoutException build() {
            return new ApiCallTimeoutException(this);
        }
    }

    public static interface Builder
    extends SdkClientException.Builder {
        @Override
        public Builder message(String var1);

        @Override
        public Builder cause(Throwable var1);

        @Override
        public Builder writableStackTrace(Boolean var1);

        @Override
        public ApiCallTimeoutException build();
    }
}

