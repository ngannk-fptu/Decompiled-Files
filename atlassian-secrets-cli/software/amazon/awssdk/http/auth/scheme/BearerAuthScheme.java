/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.scheme;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.auth.internal.scheme.DefaultBearerAuthScheme;
import software.amazon.awssdk.http.auth.signer.BearerHttpSigner;
import software.amazon.awssdk.http.auth.spi.scheme.AuthScheme;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.IdentityProviders;
import software.amazon.awssdk.identity.spi.TokenIdentity;

@SdkPublicApi
public interface BearerAuthScheme
extends AuthScheme<TokenIdentity> {
    public static final String SCHEME_ID = "smithy.api#httpBearerAuth";

    public static BearerAuthScheme create() {
        return DefaultBearerAuthScheme.create();
    }

    @Override
    public IdentityProvider<TokenIdentity> identityProvider(IdentityProviders var1);

    public BearerHttpSigner signer();
}

