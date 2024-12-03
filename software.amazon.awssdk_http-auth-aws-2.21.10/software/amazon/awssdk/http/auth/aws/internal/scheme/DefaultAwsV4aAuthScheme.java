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
import software.amazon.awssdk.http.auth.aws.scheme.AwsV4aAuthScheme;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4aHttpSigner;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.IdentityProviders;

@SdkInternalApi
public final class DefaultAwsV4aAuthScheme
implements AwsV4aAuthScheme {
    private static final DefaultAwsV4aAuthScheme DEFAULT = new DefaultAwsV4aAuthScheme();

    public static DefaultAwsV4aAuthScheme create() {
        return DEFAULT;
    }

    public String schemeId() {
        return "aws.auth#sigv4a";
    }

    @Override
    public IdentityProvider<AwsCredentialsIdentity> identityProvider(IdentityProviders providers) {
        return providers.identityProvider(AwsCredentialsIdentity.class);
    }

    @Override
    public AwsV4aHttpSigner signer() {
        return SignerSingletonHolder.INSTANCE;
    }

    private static class SignerSingletonHolder {
        private static final AwsV4aHttpSigner INSTANCE = AwsV4aHttpSigner.create();

        private SignerSingletonHolder() {
        }
    }
}

