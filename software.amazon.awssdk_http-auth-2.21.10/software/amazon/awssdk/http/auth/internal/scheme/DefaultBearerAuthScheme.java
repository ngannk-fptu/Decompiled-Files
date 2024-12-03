/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.identity.spi.IdentityProviders
 *  software.amazon.awssdk.identity.spi.TokenIdentity
 */
package software.amazon.awssdk.http.auth.internal.scheme;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.scheme.BearerAuthScheme;
import software.amazon.awssdk.http.auth.signer.BearerHttpSigner;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.IdentityProviders;
import software.amazon.awssdk.identity.spi.TokenIdentity;

@SdkInternalApi
public final class DefaultBearerAuthScheme
implements BearerAuthScheme {
    private static final DefaultBearerAuthScheme DEFAULT = new DefaultBearerAuthScheme();
    private static final BearerHttpSigner DEFAULT_SIGNER = BearerHttpSigner.create();

    public static DefaultBearerAuthScheme create() {
        return DEFAULT;
    }

    public String schemeId() {
        return "smithy.api#httpBearerAuth";
    }

    @Override
    public IdentityProvider<TokenIdentity> identityProvider(IdentityProviders providers) {
        return providers.identityProvider(TokenIdentity.class);
    }

    @Override
    public BearerHttpSigner signer() {
        return DEFAULT_SIGNER;
    }
}

