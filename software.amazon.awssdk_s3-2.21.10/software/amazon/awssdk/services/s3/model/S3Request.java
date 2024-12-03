/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.awscore.AwsRequest
 *  software.amazon.awssdk.awscore.AwsRequest$Builder
 *  software.amazon.awssdk.awscore.AwsRequest$BuilderImpl
 */
package software.amazon.awssdk.services.s3.model;

import software.amazon.awssdk.awscore.AwsRequest;

public abstract class S3Request
extends AwsRequest {
    protected S3Request(Builder builder) {
        super((AwsRequest.Builder)builder);
    }

    public abstract Builder toBuilder();

    protected static abstract class BuilderImpl
    extends AwsRequest.BuilderImpl
    implements Builder {
        protected BuilderImpl() {
        }

        protected BuilderImpl(S3Request request) {
            super((AwsRequest)request);
        }
    }

    public static interface Builder
    extends AwsRequest.Builder {
        public S3Request build();
    }
}

