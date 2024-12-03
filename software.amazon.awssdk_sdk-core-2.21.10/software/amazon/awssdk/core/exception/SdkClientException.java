/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.core.exception;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.exception.SdkException;

@SdkPublicApi
public class SdkClientException
extends SdkException {
    protected SdkClientException(Builder b) {
        super(b);
    }

    public static SdkClientException create(String message) {
        return SdkClientException.builder().message(message).build();
    }

    public static SdkClientException create(String message, Throwable cause) {
        return SdkClientException.builder().message(message).cause(cause).build();
    }

    @Override
    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    protected static class BuilderImpl
    extends SdkException.BuilderImpl
    implements Builder {
        protected BuilderImpl() {
        }

        protected BuilderImpl(SdkClientException ex) {
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
        public SdkClientException build() {
            return new SdkClientException(this);
        }
    }

    public static interface Builder
    extends SdkException.Builder {
        @Override
        public Builder message(String var1);

        @Override
        public Builder cause(Throwable var1);

        @Override
        public Builder writableStackTrace(Boolean var1);

        @Override
        public SdkClientException build();
    }
}

