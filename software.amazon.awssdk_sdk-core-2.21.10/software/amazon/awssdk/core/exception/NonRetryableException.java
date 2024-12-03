/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.core.exception;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.exception.SdkClientException;

@SdkPublicApi
public final class NonRetryableException
extends SdkClientException {
    protected NonRetryableException(Builder b) {
        super(b);
    }

    @Override
    public boolean retryable() {
        return false;
    }

    @Override
    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static NonRetryableException create(String message) {
        return NonRetryableException.builder().message(message).build();
    }

    public static NonRetryableException create(String message, Throwable cause) {
        return NonRetryableException.builder().message(message).cause(cause).build();
    }

    protected static final class BuilderImpl
    extends SdkClientException.BuilderImpl
    implements Builder {
        protected BuilderImpl() {
        }

        protected BuilderImpl(NonRetryableException ex) {
            super(ex);
        }

        @Override
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        @Override
        public String message() {
            return this.message;
        }

        @Override
        public Throwable cause() {
            return this.cause;
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
        public NonRetryableException build() {
            return new NonRetryableException(this);
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
        public NonRetryableException build();
    }
}

