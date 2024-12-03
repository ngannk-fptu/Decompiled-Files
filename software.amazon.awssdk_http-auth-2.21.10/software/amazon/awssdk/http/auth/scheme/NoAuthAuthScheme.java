/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthScheme
 *  software.amazon.awssdk.http.auth.spi.signer.HttpSigner
 *  software.amazon.awssdk.identity.spi.Identity
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.identity.spi.IdentityProviders
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

    public IdentityProvider<AnonymousIdentity> identityProvider(IdentityProviders var1);

    public HttpSigner<AnonymousIdentity> signer();

    public static interface AnonymousIdentity
    extends Identity {
    }
}

