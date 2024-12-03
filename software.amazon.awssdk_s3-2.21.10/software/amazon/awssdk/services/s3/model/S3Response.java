/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.awscore.AwsResponse
 *  software.amazon.awssdk.awscore.AwsResponse$Builder
 *  software.amazon.awssdk.awscore.AwsResponse$BuilderImpl
 *  software.amazon.awssdk.awscore.AwsResponseMetadata
 */
package software.amazon.awssdk.services.s3.model;

import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.awscore.AwsResponseMetadata;
import software.amazon.awssdk.services.s3.model.S3ResponseMetadata;

public abstract class S3Response
extends AwsResponse {
    private final S3ResponseMetadata responseMetadata;

    protected S3Response(Builder builder) {
        super((AwsResponse.Builder)builder);
        this.responseMetadata = builder.responseMetadata();
    }

    public S3ResponseMetadata responseMetadata() {
        return this.responseMetadata;
    }

    protected static abstract class BuilderImpl
    extends AwsResponse.BuilderImpl
    implements Builder {
        private S3ResponseMetadata responseMetadata;

        protected BuilderImpl() {
        }

        protected BuilderImpl(S3Response response) {
            super((AwsResponse)response);
            this.responseMetadata = response.responseMetadata();
        }

        @Override
        public S3ResponseMetadata responseMetadata() {
            return this.responseMetadata;
        }

        @Override
        public Builder responseMetadata(AwsResponseMetadata responseMetadata) {
            this.responseMetadata = S3ResponseMetadata.create(responseMetadata);
            return this;
        }
    }

    public static interface Builder
    extends AwsResponse.Builder {
        public S3Response build();

        public S3ResponseMetadata responseMetadata();

        public Builder responseMetadata(AwsResponseMetadata var1);
    }
}

