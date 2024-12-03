/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.exception;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.exception.SdkClientException;

@SdkPublicApi
public final class RetryableException
extends SdkClientException {
    protected RetryableException(Builder b) {
        super(b);
    }

    public static RetryableException create(String message) {
        return RetryableException.builder().message(message).build();
    }

    public static RetryableException create(String message, Throwable cause) {
        return RetryableException.builder().message(message).cause(cause).build();
    }

    @Override
    public boolean retryable() {
        return true;
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

        protected BuilderImpl(RetryableException ex) {
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
        public RetryableException build() {
            return new RetryableException(this);
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
        public RetryableException build();
    }
}

