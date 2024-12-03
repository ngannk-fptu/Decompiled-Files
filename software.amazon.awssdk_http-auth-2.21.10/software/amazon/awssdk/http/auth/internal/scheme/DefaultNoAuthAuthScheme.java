/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.auth.spi.signer.AsyncSignRequest
 *  software.amazon.awssdk.http.auth.spi.signer.AsyncSignedRequest
 *  software.amazon.awssdk.http.auth.spi.signer.HttpSigner
 *  software.amazon.awssdk.http.auth.spi.signer.SignRequest
 *  software.amazon.awssdk.http.auth.spi.signer.SignedRequest
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.identity.spi.IdentityProviders
 *  software.amazon.awssdk.identity.spi.ResolveIdentityRequest
 */
package software.amazon.awssdk.http.auth.internal.scheme;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpRequest;
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

            public Class identityType() {
                return NoAuthAuthScheme.AnonymousIdentity.class;
            }

            public CompletableFuture<NoAuthAuthScheme.AnonymousIdentity> resolveIdentity(ResolveIdentityRequest request) {
                return CompletableFuture.completedFuture(ANONYMOUS_IDENTITY);
            }
        };
    }

    private static HttpSigner<NoAuthAuthScheme.AnonymousIdentity> noAuthSigner() {
        return new HttpSigner<NoAuthAuthScheme.AnonymousIdentity>(){

            public SignedRequest sign(final SignRequest<? extends NoAuthAuthScheme.AnonymousIdentity> request) {
                return new SignedRequest(){

                    public SdkHttpRequest request() {
                        return request.request();
                    }

                    public Optional<ContentStreamProvider> payload() {
                        return request.payload();
                    }
                };
            }

            public CompletableFuture<AsyncSignedRequest> signAsync(final AsyncSignRequest<? extends NoAuthAuthScheme.AnonymousIdentity> request) {
                AsyncSignedRequest result = new AsyncSignedRequest(){

                    public SdkHttpRequest request() {
                        return request.request();
                    }

                    public Optional<Publisher<ByteBuffer>> payload() {
                        return request.payload();
                    }
                };
                return CompletableFuture.completedFuture(result);
            }
        };
    }

    private static NoAuthAuthScheme.AnonymousIdentity anonymousIdentity() {
        return new NoAuthAuthScheme.AnonymousIdentity(){};
    }
}

