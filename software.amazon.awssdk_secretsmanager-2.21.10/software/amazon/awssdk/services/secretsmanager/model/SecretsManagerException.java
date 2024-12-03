/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.awscore.exception.AwsErrorDetails
 *  software.amazon.awssdk.awscore.exception.AwsServiceException
 *  software.amazon.awssdk.awscore.exception.AwsServiceException$Builder
 *  software.amazon.awssdk.awscore.exception.AwsServiceException$BuilderImpl
 */
package software.amazon.awssdk.services.secretsmanager.model;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

public class SecretsManagerException
extends AwsServiceException {
    protected SecretsManagerException(Builder builder) {
        super((AwsServiceException.Builder)builder);
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
    extends AwsServiceException.BuilderImpl
    implements Builder {
        protected BuilderImpl() {
        }

        protected BuilderImpl(SecretsManagerException ex) {
            super((AwsServiceException)ex);
        }

        @Override
        public BuilderImpl awsErrorDetails(AwsErrorDetails awsErrorDetails) {
            this.awsErrorDetails = awsErrorDetails;
            return this;
        }

        @Override
        public BuilderImpl message(String message) {
            this.message = message;
            return this;
        }

        @Override
        public BuilderImpl requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        @Override
        public BuilderImpl statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        @Override
        public BuilderImpl cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        @Override
        public BuilderImpl writableStackTrace(Boolean writableStackTrace) {
            this.writableStackTrace = writableStackTrace;
            return this;
        }

        public SecretsManagerException build() {
            return new SecretsManagerException(this);
        }
    }

    public static interface Builder
    extends AwsServiceException.Builder {
        public Builder awsErrorDetails(AwsErrorDetails var1);

        public Builder message(String var1);

        public Builder requestId(String var1);

        public Builder statusCode(int var1);

        public Builder cause(Throwable var1);

        public Builder writableStackTrace(Boolean var1);
    }
}

