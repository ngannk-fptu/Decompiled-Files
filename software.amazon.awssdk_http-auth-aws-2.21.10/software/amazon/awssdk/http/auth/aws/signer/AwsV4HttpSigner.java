/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.http.auth.spi.signer.SignerProperty
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 */
package software.amazon.awssdk.http.auth.aws.signer;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.auth.aws.internal.signer.DefaultAwsV4HttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4FamilyHttpSigner;
import software.amazon.awssdk.http.auth.spi.signer.SignerProperty;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;

@SdkPublicApi
public interface AwsV4HttpSigner
extends AwsV4FamilyHttpSigner<AwsCredentialsIdentity> {
    public static final SignerProperty<String> REGION_NAME = SignerProperty.create(AwsV4HttpSigner.class, (String)"RegionName");

    public static AwsV4HttpSigner create() {
        return new DefaultAwsV4HttpSigner();
    }
}

