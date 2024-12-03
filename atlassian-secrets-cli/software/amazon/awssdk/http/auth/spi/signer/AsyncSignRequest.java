/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.spi.signer;

import java.nio.ByteBuffer;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.http.auth.spi.internal.signer.DefaultAsyncSignRequest;
import software.amazon.awssdk.http.auth.spi.signer.BaseSignRequest;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public interface AsyncSignRequest<IdentityT extends Identity>
extends BaseSignRequest<Publisher<ByteBuffer>, IdentityT>,
ToCopyableBuilder<Builder<IdentityT>, AsyncSignRequest<IdentityT>> {
    public static <IdentityT extends Identity> Builder<IdentityT> builder(IdentityT identity) {
        return DefaultAsyncSignRequest.builder(identity);
    }

    public static interface Builder<IdentityT extends Identity>
    extends BaseSignRequest.Builder<Builder<IdentityT>, Publisher<ByteBuffer>, IdentityT>,
    CopyableBuilder<Builder<IdentityT>, AsyncSignRequest<IdentityT>> {
    }
}

