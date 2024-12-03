/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.internal.signer;

import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.signer.BearerHttpSigner;
import software.amazon.awssdk.http.auth.spi.signer.AsyncSignRequest;
import software.amazon.awssdk.http.auth.spi.signer.AsyncSignedRequest;
import software.amazon.awssdk.http.auth.spi.signer.BaseSignRequest;
import software.amazon.awssdk.http.auth.spi.signer.SignRequest;
import software.amazon.awssdk.http.auth.spi.signer.SignedRequest;
import software.amazon.awssdk.identity.spi.TokenIdentity;

@SdkInternalApi
public final class DefaultBearerHttpSigner
implements BearerHttpSigner {
    @Override
    public SignedRequest sign(SignRequest<? extends TokenIdentity> request) {
        return (SignedRequest)((SignedRequest.Builder)((SignedRequest.Builder)SignedRequest.builder().request(this.doSign(request))).payload(request.payload().orElse(null))).build();
    }

    @Override
    public CompletableFuture<AsyncSignedRequest> signAsync(AsyncSignRequest<? extends TokenIdentity> request) {
        return CompletableFuture.completedFuture(((AsyncSignedRequest.Builder)((AsyncSignedRequest.Builder)AsyncSignedRequest.builder().request(this.doSign(request))).payload(request.payload().orElse(null))).build());
    }

    private SdkHttpRequest doSign(BaseSignRequest<?, ? extends TokenIdentity> request) {
        return (SdkHttpRequest)((SdkHttpRequest.Builder)request.request().toBuilder()).putHeader("Authorization", this.buildAuthorizationHeader(request.identity())).build();
    }

    private String buildAuthorizationHeader(TokenIdentity tokenIdentity) {
        return "Bearer " + tokenIdentity.token();
    }
}

