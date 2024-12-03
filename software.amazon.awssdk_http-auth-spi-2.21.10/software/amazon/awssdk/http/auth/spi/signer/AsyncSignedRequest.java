/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.utils.builder.SdkBuilder
 */
package software.amazon.awssdk.http.auth.spi.signer;

import java.nio.ByteBuffer;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.http.auth.spi.internal.signer.DefaultAsyncSignedRequest;
import software.amazon.awssdk.http.auth.spi.signer.BaseSignedRequest;
import software.amazon.awssdk.utils.builder.SdkBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public interface AsyncSignedRequest
extends BaseSignedRequest<Publisher<ByteBuffer>> {
    public static Builder builder() {
        return new DefaultAsyncSignedRequest.BuilderImpl();
    }

    public static interface Builder
    extends BaseSignedRequest.Builder<Builder, Publisher<ByteBuffer>>,
    SdkBuilder<Builder, AsyncSignedRequest> {
    }
}

