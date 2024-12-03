/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.builder.Buildable
 */
package software.amazon.awssdk.core.exception;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.builder.Buildable;

@SdkPublicApi
public class SdkException
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    protected SdkException(Builder builder) {
        super(SdkException.messageFromBuilder(builder), builder.cause(), true, SdkException.writableStackTraceFromBuilder(builder));
    }

    private static String messageFromBuilder(Builder builder) {
        if (builder.message() != null) {
            return builder.message();
        }
        if (builder.cause() != null) {
            return builder.cause().getMessage();
        }
        return null;
    }

    private static boolean writableStackTraceFromBuilder(Builder builder) {
        return builder.writableStackTrace() == null || builder.writableStackTrace() != false;
    }

    public static SdkException create(String message, Throwable cause) {
        return SdkException.builder().message(message).cause(cause).build();
    }

    public boolean retryable() {
        return false;
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    protected static class BuilderImpl
    implements Builder {
        protected Throwable cause;
        protected String message;
        protected Boolean writableStackTrace;

        protected BuilderImpl() {
        }

        protected BuilderImpl(SdkException ex) {
            this.cause = ex.getCause();
            this.message = ex.getMessage();
        }

        public Throwable getCause() {
            return this.cause;
        }

        public void setCause(Throwable cause) {
            this.cause = cause;
        }

        @Override
        public Builder cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        @Override
        public Throwable cause() {
            return this.cause;
        }

        public String getMessage() {
            return this.message;
        }

        @Override
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String message() {
            return this.message;
        }

        @Override
        public Builder writableStackTrace(Boolean writableStackTrace) {
            this.writableStackTrace = writableStackTrace;
            return this;
        }

        public void setWritableStackTrace(Boolean writableStackTrace) {
            this.writableStackTrace = writableStackTrace;
        }

        @Override
        public Boolean writableStackTrace() {
            return this.writableStackTrace;
        }

        public Boolean getWritableStackTrace() {
            return this.writableStackTrace;
        }

        @Override
        public SdkException build() {
            return new SdkException(this);
        }
    }

    public static interface Builder
    extends Buildable {
        public Builder cause(Throwable var1);

        public Throwable cause();

        public Builder message(String var1);

        public String message();

        public Builder writableStackTrace(Boolean var1);

        public Boolean writableStackTrace();

        public SdkException build();
    }
}

