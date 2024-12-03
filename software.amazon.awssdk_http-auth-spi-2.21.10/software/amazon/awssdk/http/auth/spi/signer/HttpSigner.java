/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.identity.spi.Identity
 */
package software.amazon.awssdk.http.auth.spi.signer;

import java.time.Clock;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.auth.spi.internal.signer.DefaultAsyncSignRequest;
import software.amazon.awssdk.http.auth.spi.internal.signer.DefaultSignRequest;
import software.amazon.awssdk.http.auth.spi.signer.AsyncSignRequest;
import software.amazon.awssdk.http.auth.spi.signer.AsyncSignedRequest;
import software.amazon.awssdk.http.auth.spi.signer.SignRequest;
import software.amazon.awssdk.http.auth.spi.signer.SignedRequest;
import software.amazon.awssdk.http.auth.spi.signer.SignerProperty;
import software.amazon.awssdk.identity.spi.Identity;

@SdkPublicApi
public interface HttpSigner<IdentityT extends Identity> {
    public static final SignerProperty<Clock> SIGNING_CLOCK = SignerProperty.create(HttpSigner.class, "SigningClock");

    public SignedRequest sign(SignRequest<? extends IdentityT> var1);

    default public SignedRequest sign(Consumer<SignRequest.Builder<IdentityT>> consumer) {
        return this.sign((SignRequest)((SignRequest.Builder)DefaultSignRequest.builder().applyMutation(consumer)).build());
    }

    public CompletableFuture<AsyncSignedRequest> signAsync(AsyncSignRequest<? extends IdentityT> var1);

    default public CompletableFuture<AsyncSignedRequest> signAsync(Consumer<AsyncSignRequest.Builder<IdentityT>> consumer) {
        return this.signAsync((AsyncSignRequest)((AsyncSignRequest.Builder)DefaultAsyncSignRequest.builder().applyMutation(consumer)).build());
    }
}

