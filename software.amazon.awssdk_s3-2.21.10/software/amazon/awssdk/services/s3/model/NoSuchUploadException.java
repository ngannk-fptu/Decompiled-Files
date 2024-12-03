/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.awscore.exception.AwsErrorDetails
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class NoSuchUploadException
extends S3Exception
implements ToCopyableBuilder<Builder, NoSuchUploadException> {
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(new SdkField[0]));
    private static final long serialVersionUID = 1L;

    private NoSuchUploadException(BuilderImpl builder) {
        super(builder);
    }

    @Override
    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static Class<? extends Builder> serializableBuilderClass() {
        return BuilderImpl.class;
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    static final class BuilderImpl
    extends S3Exception.BuilderImpl
    implements Builder {
        private BuilderImpl() {
        }

        private BuilderImpl(NoSuchUploadException model) {
            super(model);
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

        @Override
        public NoSuchUploadException build() {
            return new NoSuchUploadException(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, NoSuchUploadException>,
    S3Exception.Builder {
        @Override
        public Builder awsErrorDetails(AwsErrorDetails var1);

        @Override
        public Builder message(String var1);

        @Override
        public Builder requestId(String var1);

        @Override
        public Builder statusCode(int var1);

        @Override
        public Builder cause(Throwable var1);

        @Override
        public Builder writableStackTrace(Boolean var1);
    }
}

