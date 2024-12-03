/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.internal.scheme;

import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.scheme.NoAuthAuthScheme;
import software.amazon.awssdk.http.auth.spi.signer.AsyncSignRequest;
import software.amazon.awssdk.http.auth.spi.signer.AsyncSignedRequest;
import software.amazon.awssdk.http.auth.spi.signer.HttpSigner;
import software.amazon.awssdk.http.auth.spi.signer.SignRequest;
import software.amazon.awssdk.http.auth.spi.signer.SignedRequest;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.IdentityProviders;
import software.amazon.awssdk.identity.spi.ResolveIdentityRequest;

@SdkInternalApi
public final class DefaultNoAuthAuthScheme
implements NoAuthAuthScheme {
    private static final DefaultNoAuthAuthScheme DEFAULT = new DefaultNoAuthAuthScheme();
    private static final IdentityProvider<NoAuthAuthScheme.AnonymousIdentity> DEFAULT_IDENTITY_PROVIDER = DefaultNoAuthAuthScheme.noAuthIdentityProvider();
    private static final HttpSigner<NoAuthAuthScheme.AnonymousIdentity> DEFAULT_SIGNER = DefaultNoAuthAuthScheme.noAuthSigner();
    private static final NoAuthAuthScheme.AnonymousIdentity ANONYMOUS_IDENTITY = DefaultNoAuthAuthScheme.anonymousIdentity();

    public static NoAuthAuthScheme create() {
        return DEFAULT;
    }

    @Override
    public String schemeId() {
        return "smithy.api#noAuth";
    }

    @Override
    public IdentityProvider<NoAuthAuthScheme.AnonymousIdentity> identityProvider(IdentityProviders providers) {
        return DEFAULT_IDENTITY_PROVIDER;
    }

    @Override
    public HttpSigner<NoAuthAuthScheme.AnonymousIdentity> signer() {
        return DEFAULT_SIGNER;
    }

    private static IdentityProvider<NoAuthAuthScheme.AnonymousIdentity> noAuthIdentityProvider() {
        return new IdentityProvider<NoAuthAuthScheme.AnonymousIdentity>(){

            @Override
            public Class identityType() {
                return NoAuthAuthScheme.AnonymousIdentity.class;
            }

            @Override
            public CompletableFuture<NoAuthAuthScheme.AnonymousIdentity> resolveIdentity(ResolveIdentityRequest request) {
                return CompletableFuture.completedFuture(ANONYMOUS_IDENTITY);
            }
        };
    }

    private static HttpSigner<NoAuthAuthScheme.AnonymousIdentity> noAuthSigner() {
        return new HttpSigner<NoAuthAuthScheme.AnonymousIdentity>(){

            @Override
            public SignedRequest sign(SignRequest<? extends NoAuthAuthScheme.AnonymousIdentity> request) {
                return (SignedRequest)((SignedRequest.Builder)((SignedRequest.Builder)SignedRequest.builder().request(request.request())).payload(request.payload().orElse(null))).build();
            }

            @Override
            public CompletableFuture<AsyncSignedRequest> signAsync(AsyncSignRequest<? extends NoAuthAuthScheme.AnonymousIdentity> request) {
                return CompletableFuture.completedFuture(((AsyncSignedRequest.Builder)((AsyncSignedRequest.Builder)AsyncSignedRequest.builder().request(request.request())).payload(request.payload().orElse(null))).build());
            }
        };
    }

    private static NoAuthAuthScheme.AnonymousIdentity anonymousIdentity() {
        return new NoAuthAuthScheme.AnonymousIdentity(){};
    }
}

