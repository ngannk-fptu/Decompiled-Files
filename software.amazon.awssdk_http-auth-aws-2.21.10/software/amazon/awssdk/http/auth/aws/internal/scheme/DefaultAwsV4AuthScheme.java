/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.identity.spi.IdentityProviders
 */
package software.amazon.awssdk.http.auth.aws.internal.scheme;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.scheme.AwsV4AuthScheme;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.IdentityProviders;

@SdkInternalApi
public final class DefaultAwsV4AuthScheme
implements AwsV4AuthScheme {
    private static final DefaultAwsV4AuthScheme DEFAULT = new DefaultAwsV4AuthScheme();
    private static final AwsV4HttpSigner DEFAULT_SIGNER = AwsV4HttpSigner.create();

    public static DefaultAwsV4AuthScheme create() {
        return DEFAULT;
    }

    public String schemeId() {
        return "aws.auth#sigv4";
    }

    @Override
    public IdentityProvider<AwsCredentialsIdentity> identityProvider(IdentityProviders providers) {
        return providers.identityProvider(AwsCredentialsIdentity.class);
    }

    @Override
    public AwsV4HttpSigner signer() {
        return DEFAULT_SIGNER;
    }
}

