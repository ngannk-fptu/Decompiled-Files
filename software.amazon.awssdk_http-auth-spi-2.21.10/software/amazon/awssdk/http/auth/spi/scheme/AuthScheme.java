/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.identity.spi.Identity
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.identity.spi.IdentityProviders
 */
package software.amazon.awssdk.http.auth.spi.scheme;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.auth.spi.signer.HttpSigner;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.IdentityProviders;

@SdkPublicApi
public interface AuthScheme<T extends Identity> {
    public String schemeId();

    public IdentityProvider<T> identityProvider(IdentityProviders var1);

    public HttpSigner<T> signer();
}

