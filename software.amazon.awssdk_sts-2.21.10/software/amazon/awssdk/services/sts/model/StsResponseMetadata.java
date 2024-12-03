/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.awscore.AwsResponseMetadata
 */
package software.amazon.awssdk.services.sts.model;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.awscore.AwsResponseMetadata;

@SdkPublicApi
public final class StsResponseMetadata
extends AwsResponseMetadata {
    private StsResponseMetadata(AwsResponseMetadata responseMetadata) {
        super(responseMetadata);
    }

    public static StsResponseMetadata create(AwsResponseMetadata responseMetadata) {
        return new StsResponseMetadata(responseMetadata);
    }
}

