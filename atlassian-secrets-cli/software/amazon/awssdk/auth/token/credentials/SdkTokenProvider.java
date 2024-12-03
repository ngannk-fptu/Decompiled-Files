/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.token.credentials;

import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.token.credentials.SdkToken;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.ResolveIdentityRequest;
import software.amazon.awssdk.identity.spi.TokenIdentity;

@FunctionalInterface
@SdkPublicApi
public interface SdkTokenProvider
extends IdentityProvider<TokenIdentity> {
    public SdkToken resolveToken();

    @Override
    default public Class<TokenIdentity> identityType() {
        return TokenIdentity.class;
    }

    @Override
    default public CompletableFuture<TokenIdentity> resolveIdentity(ResolveIdentityRequest request) {
        return CompletableFuture.completedFuture(this.resolveToken());
    }
}

