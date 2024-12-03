/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.scheme;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.auth.aws.internal.scheme.DefaultAwsV4aAuthScheme;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4aHttpSigner;
import software.amazon.awssdk.http.auth.spi.scheme.AuthScheme;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.IdentityProviders;

@SdkPublicApi
public interface AwsV4aAuthScheme
extends AuthScheme<AwsCredentialsIdentity> {
    public static final String SCHEME_ID = "aws.auth#sigv4a";

    public static AwsV4aAuthScheme create() {
        return DefaultAwsV4aAuthScheme.create();
    }

    @Override
    public IdentityProvider<AwsCredentialsIdentity> identityProvider(IdentityProviders var1);

    public AwsV4aHttpSigner signer();
}

