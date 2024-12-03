/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.awscore.AwsResponse
 *  software.amazon.awssdk.awscore.AwsResponse$Builder
 *  software.amazon.awssdk.awscore.AwsResponse$BuilderImpl
 *  software.amazon.awssdk.awscore.AwsResponseMetadata
 */
package software.amazon.awssdk.services.sts.model;

import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.awscore.AwsResponseMetadata;
import software.amazon.awssdk.services.sts.model.StsResponseMetadata;

public abstract class StsResponse
extends AwsResponse {
    private final StsResponseMetadata responseMetadata;

    protected StsResponse(Builder builder) {
        super((AwsResponse.Builder)builder);
        this.responseMetadata = builder.responseMetadata();
    }

    public StsResponseMetadata responseMetadata() {
        return this.responseMetadata;
    }

    protected static abstract class BuilderImpl
    extends AwsResponse.BuilderImpl
    implements Builder {
        private StsResponseMetadata responseMetadata;

        protected BuilderImpl() {
        }

        protected BuilderImpl(StsResponse response) {
            super((AwsResponse)response);
            this.responseMetadata = response.responseMetadata();
        }

        @Override
        public StsResponseMetadata responseMetadata() {
            return this.responseMetadata;
        }

        @Override
        public Builder responseMetadata(AwsResponseMetadata responseMetadata) {
            this.responseMetadata = StsResponseMetadata.create(responseMetadata);
            return this;
        }
    }

    public static interface Builder
    extends AwsResponse.Builder {
        public StsResponse build();

        public StsResponseMetadata responseMetadata();

        public Builder responseMetadata(AwsResponseMetadata var1);
    }
}

