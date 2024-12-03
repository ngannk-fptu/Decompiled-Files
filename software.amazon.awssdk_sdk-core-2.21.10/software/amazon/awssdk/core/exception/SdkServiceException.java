/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.core.exception;

import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.exception.SdkException;

@SdkPublicApi
public class SdkServiceException
extends SdkException
implements SdkPojo {
    private final String requestId;
    private final String extendedRequestId;
    private final int statusCode;

    protected SdkServiceException(Builder b) {
        super(b);
        this.requestId = b.requestId();
        this.extendedRequestId = b.extendedRequestId();
        this.statusCode = b.statusCode();
    }

    public String requestId() {
        return this.requestId;
    }

    public String extendedRequestId() {
        return this.extendedRequestId;
    }

    public int statusCode() {
        return this.statusCode;
    }

    public boolean isClockSkewException() {
        return false;
    }

    public boolean isThrottlingException() {
        return this.statusCode == 429;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    @Override
    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Class<? extends Builder> serializableBuilderClass() {
        return BuilderImpl.class;
    }

    @Override
    public List<SdkField<?>> sdkFields() {
        return Collections.emptyList();
    }

    protected static class BuilderImpl
    extends SdkException.BuilderImpl
    implements Builder {
        protected String requestId;
        protected String extendedRequestId;
        protected int statusCode;

        protected BuilderImpl() {
        }

        protected BuilderImpl(SdkServiceException ex) {
            super(ex);
            this.requestId = ex.requestId();
            this.extendedRequestId = ex.extendedRequestId();
            this.statusCode = ex.statusCode();
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
        public Builder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        @Override
        public Builder extendedRequestId(String extendedRequestId) {
            this.extendedRequestId = extendedRequestId;
            return this;
        }

        @Override
        public String requestId() {
            return this.requestId;
        }

        public String getRequestId() {
            return this.requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        @Override
        public String extendedRequestId() {
            return this.extendedRequestId;
        }

        public String getExtendedRequestId() {
            return this.extendedRequestId;
        }

        public void setExtendedRequestId(String extendedRequestId) {
            this.extendedRequestId = extendedRequestId;
        }

        @Override
        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public int getStatusCode() {
            return this.statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        @Override
        public int statusCode() {
            return this.statusCode;
        }

        @Override
        public SdkServiceException build() {
            return new SdkServiceException(this);
        }

        @Override
        public List<SdkField<?>> sdkFields() {
            return Collections.emptyList();
        }
    }

    public static interface Builder
    extends SdkException.Builder,
    SdkPojo {
        @Override
        public Builder message(String var1);

        @Override
        public Builder cause(Throwable var1);

        @Override
        public Builder writableStackTrace(Boolean var1);

        public Builder requestId(String var1);

        public String requestId();

        public Builder extendedRequestId(String var1);

        public String extendedRequestId();

        public Builder statusCode(int var1);

        public int statusCode();

        @Override
        public SdkServiceException build();
    }
}

