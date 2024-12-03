/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.SdkHttpRequest$Builder
 *  software.amazon.awssdk.http.auth.spi.signer.AsyncSignRequest
 *  software.amazon.awssdk.http.auth.spi.signer.AsyncSignedRequest
 *  software.amazon.awssdk.http.auth.spi.signer.AsyncSignedRequest$Builder
 *  software.amazon.awssdk.http.auth.spi.signer.BaseSignRequest
 *  software.amazon.awssdk.http.auth.spi.signer.SignRequest
 *  software.amazon.awssdk.http.auth.spi.signer.SignedRequest
 *  software.amazon.awssdk.http.auth.spi.signer.SignedRequest$Builder
 *  software.amazon.awssdk.identity.spi.TokenIdentity
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
    public SignedRequest sign(SignRequest<? extends TokenIdentity> request) {
        return (SignedRequest)((SignedRequest.Builder)((SignedRequest.Builder)SignedRequest.builder().request(this.doSign((BaseSignRequest<?, ? extends TokenIdentity>)request))).payload(request.payload().orElse(null))).build();
    }

    public CompletableFuture<AsyncSignedRequest> signAsync(AsyncSignRequest<? extends TokenIdentity> request) {
        return CompletableFuture.completedFuture(((AsyncSignedRequest.Builder)((AsyncSignedRequest.Builder)AsyncSignedRequest.builder().request(this.doSign((BaseSignRequest<?, ? extends TokenIdentity>)request))).payload(request.payload().orElse(null))).build());
    }

    private SdkHttpRequest doSign(BaseSignRequest<?, ? extends TokenIdentity> request) {
        return (SdkHttpRequest)((SdkHttpRequest.Builder)request.request().toBuilder()).putHeader("Authorization", this.buildAuthorizationHeader((TokenIdentity)request.identity())).build();
    }

    private String buildAuthorizationHeader(TokenIdentity tokenIdentity) {
        return "Bearer " + tokenIdentity.token();
    }
}

