/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.async;

import java.nio.ByteBuffer;
import java.util.Optional;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkPublicApi;

@SdkPublicApi
public interface SdkHttpContentPublisher
extends Publisher<ByteBuffer> {
    public Optional<Long> contentLength();
}

