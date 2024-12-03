/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.awscore.AwsResponseMetadata
 */
package software.amazon.awssdk.services.s3.model;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.awscore.AwsResponseMetadata;

@SdkPublicApi
public final class S3ResponseMetadata
extends AwsResponseMetadata {
    private static final String CLOUD_FRONT_ID = "X-Amz-Cf-Id";
    private static final String EXTENDED_REQUEST_ID = "x-amz-id-2";
    private static final String REQUEST_ID = "x-amz-request-id";

    private S3ResponseMetadata(AwsResponseMetadata responseMetadata) {
        super(responseMetadata);
    }

    public static S3ResponseMetadata create(AwsResponseMetadata responseMetadata) {
        return new S3ResponseMetadata(responseMetadata);
    }

    public String cloudFrontId() {
        return this.getValue(CLOUD_FRONT_ID);
    }

    public String extendedRequestId() {
        return this.getValue(EXTENDED_REQUEST_ID);
    }

    public String requestId() {
        return this.getValue(REQUEST_ID);
    }
}

