/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.core.exception.SdkServiceException
 *  software.amazon.awssdk.core.exception.SdkServiceException$Builder
 *  software.amazon.awssdk.core.exception.SdkServiceException$BuilderImpl
 *  software.amazon.awssdk.core.retry.ClockSkew
 *  software.amazon.awssdk.http.SdkHttpResponse
 */
package software.amazon.awssdk.awscore.exception;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.StringJoiner;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.internal.AwsErrorCode;
import software.amazon.awssdk.awscore.internal.AwsStatusCode;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.core.retry.ClockSkew;
import software.amazon.awssdk.http.SdkHttpResponse;

@SdkPublicApi
public class AwsServiceException
extends SdkServiceException {
    private AwsErrorDetails awsErrorDetails;
    private Duration clockSkew;

    protected AwsServiceException(Builder b) {
        super((SdkServiceException.Builder)b);
        this.awsErrorDetails = b.awsErrorDetails();
        this.clockSkew = b.clockSkew();
    }

    public AwsErrorDetails awsErrorDetails() {
        return this.awsErrorDetails;
    }

    public String getMessage() {
        if (this.awsErrorDetails != null) {
            String message;
            StringJoiner details = new StringJoiner(", ", "(", ")");
            details.add("Service: " + this.awsErrorDetails().serviceName());
            details.add("Status Code: " + this.statusCode());
            details.add("Request ID: " + this.requestId());
            if (this.extendedRequestId() != null) {
                details.add("Extended Request ID: " + this.extendedRequestId());
            }
            if ((message = super.getMessage()) == null) {
                message = this.awsErrorDetails().errorMessage();
            }
            return message + " " + details;
        }
        return super.getMessage();
    }

    public boolean isClockSkewException() {
        if (super.isClockSkewException()) {
            return true;
        }
        if (this.awsErrorDetails == null) {
            return false;
        }
        if (AwsErrorCode.isDefiniteClockSkewErrorCode(this.awsErrorDetails.errorCode())) {
            return true;
        }
        SdkHttpResponse sdkHttpResponse = this.awsErrorDetails.sdkHttpResponse();
        if (this.clockSkew == null || sdkHttpResponse == null) {
            return false;
        }
        boolean isPossibleClockSkewError = AwsErrorCode.isPossibleClockSkewErrorCode(this.awsErrorDetails.errorCode()) || AwsStatusCode.isPossibleClockSkewStatusCode(this.statusCode());
        return isPossibleClockSkewError && ClockSkew.isClockSkewed((Instant)Instant.now().minus(this.clockSkew), (Instant)ClockSkew.getServerTime((SdkHttpResponse)sdkHttpResponse).orElse(null));
    }

    public boolean isThrottlingException() {
        return super.isThrottlingException() || Optional.ofNullable(this.awsErrorDetails).map(a -> AwsErrorCode.isThrottlingErrorCode(a.errorCode())).orElse(false) != false;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Class<? extends Builder> serializableBuilderClass() {
        return BuilderImpl.class;
    }

    protected static class BuilderImpl
    extends SdkServiceException.BuilderImpl
    implements Builder {
        protected AwsErrorDetails awsErrorDetails;
        private Duration clockSkew;

        protected BuilderImpl() {
        }

        protected BuilderImpl(AwsServiceException ex) {
            super((SdkServiceException)ex);
            this.awsErrorDetails = ex.awsErrorDetails();
        }

        @Override
        public Builder awsErrorDetails(AwsErrorDetails awsErrorDetails) {
            this.awsErrorDetails = awsErrorDetails;
            return this;
        }

        @Override
        public AwsErrorDetails awsErrorDetails() {
            return this.awsErrorDetails;
        }

        public AwsErrorDetails getAwsErrorDetails() {
            return this.awsErrorDetails;
        }

        public void setAwsErrorDetails(AwsErrorDetails awsErrorDetails) {
            this.awsErrorDetails = awsErrorDetails;
        }

        @Override
        public Builder clockSkew(Duration clockSkew) {
            this.clockSkew = clockSkew;
            return this;
        }

        @Override
        public Duration clockSkew() {
            return this.clockSkew;
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
        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        @Override
        public AwsServiceException build() {
            return new AwsServiceException(this);
        }
    }

    public static interface Builder
    extends SdkServiceException.Builder {
        public Builder awsErrorDetails(AwsErrorDetails var1);

        public AwsErrorDetails awsErrorDetails();

        public Builder clockSkew(Duration var1);

        public Duration clockSkew();

        public Builder message(String var1);

        public Builder cause(Throwable var1);

        public Builder requestId(String var1);

        public Builder extendedRequestId(String var1);

        public Builder statusCode(int var1);

        public AwsServiceException build();
    }
}

