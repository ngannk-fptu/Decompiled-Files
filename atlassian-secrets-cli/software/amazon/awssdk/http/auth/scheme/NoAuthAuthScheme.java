/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.scheme;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.auth.internal.scheme.DefaultNoAuthAuthScheme;
import software.amazon.awssdk.http.auth.spi.scheme.AuthScheme;
import software.amazon.awssdk.http.auth.spi.signer.HttpSigner;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.IdentityProviders;

@SdkPublicApi
public interface NoAuthAuthScheme
extends AuthScheme<AnonymousIdentity> {
    public static final String SCHEME_ID = "smithy.api#noAuth";

    public static NoAuthAuthScheme create() {
        return DefaultNoAuthAuthScheme.create();
    }

    @Override
    public IdentityProvider<AnonymousIdentity> identityProvider(IdentityProviders var1);

    @Override
    public HttpSigner<AnonymousIdentity> signer();

    public static interface AnonymousIdentity
    extends Identity {
    }
}

