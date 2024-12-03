/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.spi.signer;

import java.nio.ByteBuffer;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.http.auth.spi.internal.signer.DefaultAsyncSignedRequest;
import software.amazon.awssdk.http.auth.spi.signer.BaseSignedRequest;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public interface AsyncSignedRequest
extends BaseSignedRequest<Publisher<ByteBuffer>>,
ToCopyableBuilder<Builder, AsyncSignedRequest> {
    public static Builder builder() {
        return DefaultAsyncSignedRequest.builder();
    }

    public static interface Builder
    extends BaseSignedRequest.Builder<Builder, Publisher<ByteBuffer>>,
    CopyableBuilder<Builder, AsyncSignedRequest> {
    }
}

