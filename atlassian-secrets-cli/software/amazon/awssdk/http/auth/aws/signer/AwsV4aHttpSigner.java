/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.signer;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.OptionalDependencyLoaderUtil;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4FamilyHttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.RegionSet;
import software.amazon.awssdk.http.auth.spi.signer.SignerProperty;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;

@SdkPublicApi
public interface AwsV4aHttpSigner
extends AwsV4FamilyHttpSigner<AwsCredentialsIdentity> {
    public static final SignerProperty<RegionSet> REGION_SET = SignerProperty.create(AwsV4aHttpSigner.class, "RegionSet");

    public static AwsV4aHttpSigner create() {
        return OptionalDependencyLoaderUtil.getDefaultAwsCrtV4aHttpSigner();
    }
}

