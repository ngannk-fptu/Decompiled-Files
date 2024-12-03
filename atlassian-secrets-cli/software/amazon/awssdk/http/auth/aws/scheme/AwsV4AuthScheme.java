/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.scheme;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.auth.aws.internal.scheme.DefaultAwsV4AuthScheme;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner;
import software.amazon.awssdk.http.auth.spi.scheme.AuthScheme;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.IdentityProviders;

@SdkPublicApi
public interface AwsV4AuthScheme
extends AuthScheme<AwsCredentialsIdentity> {
    public static final String SCHEME_ID = "aws.auth#sigv4";

    public static AwsV4AuthScheme create() {
        return DefaultAwsV4AuthScheme.create();
    }

    @Override
    public IdentityProvider<AwsCredentialsIdentity> identityProvider(IdentityProviders var1);

    public AwsV4HttpSigner signer();
}

