/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.model;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.awscore.AwsResponseMetadata;

@SdkPublicApi
public final class SecretsManagerResponseMetadata
extends AwsResponseMetadata {
    private SecretsManagerResponseMetadata(AwsResponseMetadata responseMetadata) {
        super(responseMetadata);
    }

    public static SecretsManagerResponseMetadata create(AwsResponseMetadata responseMetadata) {
        return new SecretsManagerResponseMetadata(responseMetadata);
    }
}

